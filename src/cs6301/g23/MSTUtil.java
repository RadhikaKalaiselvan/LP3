package cs6301.g23;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import cs6301.g23.DMSTGraph.DMSTEdge;
import cs6301.g23.DMSTGraph.DMSTVertex;
import cs6301.g23.Graph.Vertex;
import cs6301.g23.Graph.Edge;

public class MSTUtil {

	DMSTGraph g;
	GraphUtil gUtil;
	MSTUtil(DMSTGraph g){
		this.g = g;
		gUtil = new GraphUtil(g);
	}

	public void mst(){




		//		gUtil.DfsVisit(source, dfsVisited, false);

		DMSTVertex dVertex;
		int tempWeight=0;
		int i=1;

		while(true && i<=3){
			for(Vertex v:g){
				dVertex = g.getVertex(v);
				int min = getMinWeight(v);
				dVertex.setDelta(min);
				Iterator<Edge> edgeIt = v.reverseIterator();

				while(edgeIt.hasNext()){
					Edge e = edgeIt.next();
					tempWeight = g.gh.getEdge(e).getTempWeight() - min;
					g.gh.getEdge(e).setTempWeight(tempWeight);
					System.out.println("Edge "+e+" weight "+tempWeight);
				}
			}
			//			toggleEdge(true);

			ArrayList<HashSet<Vertex>> scc = gUtil.stronglyConnectedComponents();
			int k=0;
			for(HashSet<Vertex> lv: scc){
				System.out.println("key "+k);
				for(Vertex v:lv){
					System.out.println(" "+v);
				}
				k++;
			}


			//			System.out.println("inside scc shrink call");
			for(HashSet<Vertex> l:scc){
				if(l.size()>1){
					//					System.out.println("size of hashset"+l.size());
					//					System.out.println("Before");
					//					g.printKeySet(g);
					shrinkComponent(l);
					//					System.out.println("After");
					//					g.printKeySet(g);
				}
			}
			HashSet<Graph.Vertex> dfsVisited = new HashSet<Graph.Vertex>();
			gUtil.reinitialize();
			gUtil.dfsVisit(getSource(), dfsVisited,	false,false);
			if(dfsVisited.size()==g.enabledVertexCount){  //if all enabled nodes are reachable
				System.out.println("scc is 1");
				break;
			}
			i++;
		}

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
				System.out.println("edge* "+e);
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
						//						System.out.println("outgoing :key "+dest+"value "+e);
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
						//						System.out.println("incoming :key "+source+"value "+e);
					}

				}
			}
			g.disableVertex(t);  //disabling the vertex
			System.out.println("disabled"+t);
			//			System.out.println("enabled vert "+t+"dmst "+g.gh.getVertex(t).isDisabled());
		}	
		for(Vertex vert:dest_edge.keySet()){
			Edge newEdge = g.createNewEdge(v, vert, dest_edge.get(vert));
			//			System.out.println("new edge "+newEdge+" weight "+g.gh.getEdge(newEdge).getTempWeight());
		}
		for(Vertex vert:source_edge.keySet()){
			Edge newEdge = g.createNewEdge(vert, v, source_edge.get(vert));
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
			System.out.println("source "+v+" dfs size"+dfsVisited.size()+" enable "+g.enabledVertexCount);
			for(Vertex u:dfsVisited){
				System.out.println("vertices "+u);
			}
			
			if(dfsVisited.size()==g.enabledVertexCount){
				source = v;
				break;
			}
		}
		System.out.println("returning source "+source);
		return source;
	}

	public void toggleEdge(boolean disableEdge){

		for(Map.Entry<Edge, DMSTEdge> entry:g.gh.edgeMap.entrySet()){
			if(disableEdge == true)
				g.disableEdge(entry.getKey());
			else
				g.enableEdge(entry.getKey());
		}
	}

	public Edge getMinEdge(Vertex v){
		int min = Integer.MAX_VALUE;
		Edge temp = null;
		Iterator<Edge> revIt = v.reverseIterator();
		while(revIt.hasNext()){
			Edge e = revIt.next();
			if(e.weight < min){
				temp = e;
				break;
			}
		}
		return temp;
	}
}
