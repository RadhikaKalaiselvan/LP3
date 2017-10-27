package cs6301.g23;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import cs6301.g23.DMSTGraph.DMSTEdge;
import cs6301.g23.DMSTGraph.DMSTVertex;
import cs6301.g23.Graph.Vertex;
import cs6301.g23.Graph.Edge;

public class MSTUtil {

	DMSTGraph g;
	GraphUtil gUtil;
	LinkedList<Vertex> graphImage; 
	MSTUtil(DMSTGraph g){
		this.g = g;
		gUtil = new GraphUtil(g);
		graphImage=new LinkedList<Vertex>();
	}

	public void mst(){
		DMSTVertex dVertex;
		int tempWeight=0;
		while(true){
			for(Vertex v:g){
				dVertex = g.getVertex(v);
				int min = getMinWeight(v);
				dVertex.setDelta(min);
				Iterator<Edge> edgeIt = v.reverseIterator();

				while(edgeIt.hasNext()){
					Edge e = edgeIt.next();
					tempWeight = g.gh.getEdge(e).getTempWeight() - min;
					g.gh.getEdge(e).setTempWeight(tempWeight);
					//System.out.println("Edge "+e+" weight "+tempWeight);
				}
			}

			ArrayList<HashSet<Vertex>> scc = gUtil.stronglyConnectedComponents();
			int k=0;
			for(HashSet<Vertex> lv: scc){
				System.out.println("key "+k);
				for(Vertex v:lv){
					System.out.println(" "+v);
				}
				k++;
			}
			for(HashSet<Vertex> l:scc){
				if(l.size()>1){
					shrinkComponent(l);
				}
			}
			HashSet<Graph.Vertex> dfsVisited = new HashSet<Graph.Vertex>();
			gUtil.reinitialize();
			gUtil.dfsVisit(getSource(), dfsVisited,	false,false);
			if(dfsVisited.size()==g.enabledVertexCount){  //if all enabled nodes are reachable
				System.out.println("scc is 1");
				break;
			}
		}
		System.out.println("Last super node "+g.getLastSuperNode());
		expand();
	}

	public void shrinkComponent(HashSet<Vertex> shrinkComp){
		Vertex v = g.createNewVertex(shrinkComp);
		gUtil.addVertex(v);
		HashMap<Vertex, Edge> source_edge = new HashMap<Vertex, Edge>();
		HashMap<Vertex, Edge> dest_edge = new HashMap<Vertex, Edge>();

		for(Vertex t:shrinkComp){
			DMSTVertex dVert = g.gh.getVertex(t);
			Iterator<Edge> outgoing = dVert.iterator();
			while(outgoing.hasNext()){ //disable outgoing edges

				Edge e = outgoing.next();
				Vertex dest = e.toVertex();
				if(!shrinkComp.contains(dest)){
					if(dest_edge.containsKey(dest)){
						Edge temp = dest_edge.get(dest);
						if(g.gh.getEdge(e).getTempWeight() < g.gh.getEdge(temp).getTempWeight()){
							dest_edge.replace(dest, e);
						}
					}
					else{
						dest_edge.put(dest, e);
					}

				}
			}
			Iterator<Edge> incoming = dVert.reverseIterator();
			while(incoming.hasNext()){ // disable incoming edges
				Edge e = incoming.next();
				Vertex source = e.fromVertex();
				if(!shrinkComp.contains(source)){
					if(source_edge.containsKey(source)){
						Edge temp = source_edge.get(source);
						if(g.gh.getEdge(e).getTempWeight() < g.gh.getEdge(temp).getTempWeight()){
							source_edge.replace(source, e);
						}
					}
					else{
						source_edge.put(source, e);
					}

				}
			}
			g.disableVertex(t);  //disabling the vertex
			System.out.println("disabled"+t);
		}	
		
		for(Vertex vert:dest_edge.keySet()){
			 		g.createNewEdge(v, vert, dest_edge.get(vert));
			 			//			System.out.println("new edge "+newEdge+" weight "+g.gh.getEdge(newEdge).getTempWeight());
			 		}
			 		for(Vertex vert:source_edge.keySet()){
			 		g.createNewEdge(vert, v, source_edge.get(vert));
			 			//			System.out.println("new edge "+newEdge+" weight "+g.gh.getEdge(newEdge).getTempWeight());
			 		}
		System.out.println("Created"+v);
	}

	public int getMinWeight(Vertex v){
		int min = Integer.MAX_VALUE;
		Iterator<Edge> edgeIt = v.reverseIterator();
		while(edgeIt.hasNext()){
			Edge e = edgeIt.next();
			DMSTEdge dEdge = g.gh.getEdge(e);
			min = Math.min(min, dEdge.getTempWeight());
		}
		return min;
	}

	public Vertex getSource(){
		
		Vertex source = null;
		for(Vertex v:g){
			HashSet<Graph.Vertex> dfsVisited = new HashSet<Graph.Vertex>();
			gUtil.reinitialize();
			gUtil.dfsVisit(v, dfsVisited, false,true);
			if(dfsVisited.size()==g.enabledVertexCount){
				source = v;
				break;
			}
			
		}
		gUtil.reinitialize();
		System.out.println("source "+source);
		return source;
	}
	
	public Vertex takeGraphImage(HashSet<Vertex> hv){
		Vertex source=null;
		for(Vertex v:g){
			if(!hv.contains(v)){
				graphImage.add(v);
			}else {
				//if the vertex is in complist and has foundincoming edge then it is the source
				if(g.gh.getVertex(v).foundIncoming){
					source=v;
				}
			}
		}
		return source;
	}
	
	
	public void toggleNodes(boolean disable){
		for(Vertex v:graphImage){
			DMSTVertex dVert=g.gh.getVertex(v);
			dVert.disabled=disable;
		}
	}
	
	public void disableAllEdgesOfComp(HashSet<Vertex> hv){
		for(Vertex v:hv)
		{
			System.out.println("Processing "+v);
			for(Edge e:v.adj){
				if(!(hv.contains(e.to) && hv.contains(e.from))){
					System.out.println("disabled "+e);
				g.gh.getEdge(e).disabled=true;
				}
			}
			for(Edge ed:v.revAdj){
				if(!(hv.contains(ed.to) && hv.contains(ed.from))){
				g.gh.getEdge(ed).disabled=true;
				System.out.println("disabled "+ed);
				}
			}
		}
	}
	
	public void expand(){
		expandHelper(g.getLastSuperNode());
	}
	
	public void expandHelper(Vertex superNode){
	System.out.println("Super node "+superNode);
		DMSTVertex dmstSuperNode=g.gh.getVertex(superNode);
		HashSet<Vertex> hv=dmstSuperNode.getComp();
		if(hv==null|| hv.size()==0 || !g.gh.getVertex(superNode).isSuperNode){
			return;
		}
		disableAllEdgesOfComp(hv);
		
		LinkedList<Edge> dfsPath=gUtil.getPath();
		for(Edge e:dfsPath){
			DMSTEdge de=g.gh.getEdge(e);
			Edge originalEdgeImage=de.originalEdge;
			System.out.println(" Processing  "+e+" org "+originalEdgeImage);
			if(originalEdgeImage!=null){
			g.gh.getEdge(originalEdgeImage).disabled=false;
			System.out.println("enabled "+e);
			
			Vertex toVert=originalEdgeImage.to;
			DMSTVertex dtoVertex=g.gh.getVertex(toVert);
			dtoVertex.disabled=false;
			
			if(!dtoVertex.foundIncoming){
			dtoVertex.foundIncoming=true;
			dtoVertex.mstEdge=e;
			}
			
			g.gh.getVertex(originalEdgeImage.from).disabled=false;
			}
		}
		
		g.gh.getVertex(superNode).disabled=true;
		
		g.printKeySet(g);
		
		Vertex source=takeGraphImage(hv);
		toggleNodes(true);
		
		gUtil.reinitialize();
		gUtil.dfsVisit(source,new HashSet<Vertex>(),false,false);
		
		disableAllEdgesWithinComp(hv);
		for(Edge e:gUtil.getPath())
		{
			g.gh.getEdge(e).disabled=false;
			Vertex toVert=e.to;
			DMSTVertex dtoVertex=g.gh.getVertex(toVert);
			if(!dtoVertex.foundIncoming){
				dtoVertex.foundIncoming=true;
				dtoVertex.mstEdge=e;
				}
		}
		toggleNodes(false);
		
		g.printKeySet(g);
		expandHelper(source);
	}


void disableAllEdgesWithinComp(HashSet<Vertex> hv){
	Iterator<Vertex> it=hv.iterator();
	while(it.hasNext()){
		Vertex dv=g.gh.getVertex(it.next());
		for(Edge e:dv){
			DMSTEdge ed=g.gh.getEdge(e);
			ed.disabled=true;
		}
	}
}
}