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
	MSTUtil(DMSTGraph g){
		this.g = g;
		gUtil = new GraphUtil(g);
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
					System.out.println("Edge "+e+" weight "+tempWeight);
				}
			}

			ArrayList<HashSet<Vertex>> scc = gUtil.stronglyConnectedComponents();
//			int k=0;
//			for(HashSet<Vertex> lv: scc){
//				System.out.println("key "+k);
//				for(Vertex v:lv){
//					System.out.println(" "+v);
//				}
//				k++;
//			}
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
		expand(g.size());
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
		return source;
	}
	
	public void expand(int k){
		gUtil.reinitialize();
		Vertex source = g.getVertex(k);
		if(k==g.size()){
			source = getSource();
		}
		gUtil.dfsVisit(source, new HashSet<Graph.Vertex>(),	false,false);
		Vertex lastNode = gUtil.decFinList.getLast();
		Vertex child = lastNode;
		while(child != source){
			Vertex parent = gUtil.getParent(child);
			Vertex dmstParent = g.gh.getVertex(parent);
			for(Edge e:dmstParent){
				if(e.toVertex() == child){
					DMSTVertex dmstChild = g.gh.getVertex(child);
					if(!dmstChild.foundIncoming){
						dmstChild.foundIncoming = true;
						dmstChild.mstEdge = e;
					}
					break;
				}
				
			}
			child = parent;
		}
		expand(k-1);
		
	}
}
