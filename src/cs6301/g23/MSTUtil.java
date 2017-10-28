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

	public int mst(){
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
		g.printKeySet();
		
		for(Edge e:gUtil.getPath()){
			DMSTVertex dv=g.gh.getVertex(e.to);
			dv.foundIncoming=true;
			dv.mstEdge=e;
			System.out.println(" e :"+e.weight+" mstedge "+dv.mstEdge.weight);
			g.gh.getEdge(e).isInPath=true;
		}
		
		expand(g.getLastSuperNode());
		g.printKeySet();
		return getPath(true);
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
							g.disableEdge(e);
						}
					}
					else{
						dest_edge.put(dest, e);
						g.disableEdge(e);
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
							g.disableEdge(e);
						}
					}
					else{
						source_edge.put(source, e);
						g.disableEdge(e);
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
	
	public void expand(Vertex superNode){
	
	System.out.println("Super node "+superNode);
		DMSTVertex dmstSuperNode=g.gh.getVertex(superNode);
		HashSet<Vertex> hv=dmstSuperNode.getComp();
		if(hv==null|| hv.size()==0 || !g.gh.getVertex(superNode).isSuperNode){
			System.out.println(" returned ");
			return;
		}
		Vertex sNode=g.gh.getVertex(superNode);
		for(Edge e:sNode){
			DMSTEdge deEdge=g.gh.getEdge(e);
			if(deEdge.isInPath){
			Edge orig=deEdge.originalEdge;
			System.out.println("edge:"+e+" edge image "+orig+" set isInpath");
			g.enableEdge(orig);
			g.gh.getEdge(orig).isInPath=true;
			DMSTVertex u=g.gh.getVertex(orig.to);
			u.foundIncoming=true;
			u.mstEdge=orig;
			System.out.println(" e :"+orig.weight+" mstedge "+u.mstEdge.weight);
			}
		}
		Iterator<Edge> ite=superNode.reverseIterator();
		while(ite.hasNext()){
			Edge e=ite.next();
			DMSTEdge deEdge=g.gh.getEdge(e);
			if(deEdge.isInPath){
			Edge orig=deEdge.originalEdge;
			System.out.println("edge:"+e+" edge image "+orig+" set isInpath");
			g.enableEdge(orig);
			g.gh.getEdge(orig).isInPath=true;
			DMSTVertex u=g.gh.getVertex(orig.to);
			u.foundIncoming=true;
			u.mstEdge=orig;
			System.out.println(" e :"+orig.weight+" mstedge "+u.mstEdge.weight);
			}
		}
		
		g.disableVertex(superNode);
		
		for(Vertex ve:hv){
			DMSTVertex dv=g.gh.getVertex(ve);
			System.out.println("Enable vertex ="+ve);
			g.enableVertex(dv);
		}
		
		Vertex source=takeGraphImage(hv);
		System.out.println("source for dfs "+source);
		toggleNodes(true);
		gUtil.reinitialize();
		gUtil.dfsVisit(source,new HashSet<Vertex>(),false,false);
		HashSet<Edge> path=gUtil.getPath();
		System.out.println("------------------Path------------------");
		for(Edge p:path){
			System.out.println(" "+p);
		}
		System.out.println("----------------------------------------");
		for(Vertex v:hv)
		{
			Vertex ver=g.gh.getVertex(v);
			System.out.println("        vertex :"+v);
			Iterator<Edge> eit=ver.iterator();
			while(eit.hasNext()){
				Edge e=eit.next();
				System.out.println("second part : edge ="+e);
				if(path.contains(e)){
					System.out.println("after dfs : edge:"+e+" edge image  set isInpath");
					DMSTVertex dv=g.gh.getVertex(e.to);
					dv.foundIncoming=true;
					dv.mstEdge=e;
					System.out.println(" e :"+e.weight+" mstedge "+dv.mstEdge.weight);
					g.gh.getEdge(e).isInPath=true;
				} else {
					if(!g.gh.getEdge(e).isInPath){
						System.out.println("disabled "+e);
					g.disableEdge(e);
					}
				}
			}
		}
		toggleNodes(false);
		expand(source);
	}


int getPath(boolean print){
	int mst=0;
	for(Vertex v:g){
		DMSTVertex dv=g.gh.getVertex(v);
		Edge finalEdge=dv.mstEdge;
		if(finalEdge!=null && g.gh.getEdge(finalEdge).isInPath){
		mst+=g.gh.getEdge(finalEdge).weight;
		}
		if(print){
		     System.out.print(" "+dv.mstEdge);
			}
	}
	return mst;
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