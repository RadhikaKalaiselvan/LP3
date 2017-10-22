package cs6301.g23;

import java.util.ArrayList;
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
	MSTUtil(DMSTGraph g){
		this.g = g;
	}
	
	public void mst(){
		System.out.println("inside mst call");
		GraphUtil gUtil = new GraphUtil(g);
		HashSet<Graph.Vertex> dfsVisited = new HashSet<Graph.Vertex>();
		Vertex source = null;
		for(Vertex v:g){
			if(v.adj !=null){	
				source = v;
				break;
			}
		}
		gUtil.DfsVisit(source, dfsVisited, false);
		System.out.print("end");
		DMSTVertex dVertex;
		int tempWeight=0;
		int i=1;
		System.out.println("dfs size"+dfsVisited.size());
		System.out.println("graph size"+g.size());
		gUtil.reinitialize();
		
		while(dfsVisited.size() != g.size() && i==1){
			System.out.println("inside while");
			for(Vertex v:g){
				System.out.println("inside for");
				dVertex = g.getVertex(v);
				int min = getMinWeight(v);
				dVertex.setDelta(min);
				Iterator<Edge> edgeIt = v.reverseIterator();
				
				while(edgeIt.hasNext()){
					Edge e = edgeIt.next();
					tempWeight = g.gh.getEdge(e).getTempWeight() - min;
					g.gh.getEdge(e).setTempWeight(tempWeight);
				}
			}
			toggleEdge(true);
			
			ArrayList<HashSet<Vertex>> scc = gUtil.stronglyConnectedComponents();
			System.out.println("inside scc shrink call");
			for(HashSet<Vertex> l:scc){
				if(l.size()>1){
					System.out.println("size of hashset"+l.size());
					shrinkComponent(l);
				}
			}
			i=2;
		}
		
	}
	
	public void shrinkComponent(HashSet<Vertex> shrinkComp){
		Vertex v = g.createNewVertex(shrinkComp);
		
		for(Vertex t:shrinkComp){
			for(Edge e:t){ //disable outgoing edges
				Vertex dest = e.toVertex();
				if(!shrinkComp.contains(dest)){
					g.createNewEdge(v, dest, e);
				}
			}
			Iterator<Edge> incoming = t.reverseIterator();
			while(incoming.hasNext()){ // disable incoming edges
				Edge e = incoming.next();
				Vertex source = e.fromVertex();
				if(!shrinkComp.contains(source)){
					g.createNewEdge(source, v, e);
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
