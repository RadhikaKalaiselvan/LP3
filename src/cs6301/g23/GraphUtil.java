package cs6301.g23;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cs6301.g23.DMSTGraph.DMSTEdge;
import cs6301.g23.DMSTGraph.DMSTVertex;
import cs6301.g23.Graph.Edge;
import cs6301.g23.Graph.Vertex;
import cs6301.g23.GraphUtil.*;

public class GraphUtil extends GraphHash<GraphUtilVertex,Boolean>{
	DMSTGraph g;
	public static int time;
    public static int cno;
    public static int topNum;
    int graphSize;
    LinkedList<Graph.Vertex> decFinList;
    ArrayList<HashSet<Vertex>> compList;
    LinkedList<Graph.Edge> path;
	GraphUtil(Graph g){
		super(g);
		this.g=(DMSTGraph)g;
		graphSize=g.n;
		 decFinList=new LinkedList<Graph.Vertex>();
		 this.compList=new ArrayList<HashSet<Vertex>>();
		 this.path=new LinkedList<Graph.Edge>();
		 for(Graph.Vertex u: g) {
			    putVertex(u, new GraphUtilVertex(u));
			}
	}
	
	void addVertex(Vertex u){
		putVertex(u, new GraphUtilVertex(u));
	}
	
	static class GraphUtilVertex {
		boolean seen;
		Vertex parent;
		GraphUtilVertex(Vertex u) {
		    seen = false;
		    parent = null;
		}
		
		void reinitializeVertex() {
		    seen = false;
		    parent = null;
		}
		public boolean isSeen() {
			return seen;
		}
		public void setSeen(boolean seen) {
			this.seen = seen;
		}

	}
	
	
	 
	 Graph.Vertex getParent(Vertex u) {
			return getVertex(u).parent;
		    }
	 	 
	 void reinitialize() {
			for(Vertex u: g) {
//				System.out.println("Reint called for "+u);
			    GraphUtilVertex bu = getVertex(u);
			    bu.reinitializeVertex();
			}
		this.path.clear();
		this.decFinList.clear();	
	 }
	 

	public ArrayList<HashSet<Vertex>> stronglyConnectedComponents() { 
		reinitialize();
		LinkedList<Vertex> result=new LinkedList<Vertex>(dfs(this.g.iterator(),false, false));
		this.decFinList.clear();
		reinitialize();
		this.compList.clear();
		dfs(result.iterator(),true,false);
		return this.compList;
	}
	
	public LinkedList<Graph.Vertex> dfs(Iterator<Vertex> it,boolean rev, boolean runOnAllEdges){
		topNum=g.size();
		time=0;
		cno=0;
		this.decFinList.clear();
		while(it.hasNext()){
			Vertex uVert=it.next();
			GraphUtilVertex u=getVertex(uVert);
//			System.out.println(" vertex in util "+uVert+" seen "+u.seen);
			if(!u.seen){
				cno++;
				HashSet<Vertex> lv=new HashSet<Vertex>();
				dfsVisit(uVert,lv,rev,runOnAllEdges);
				compList.add(lv);
			}
		}
		return decFinList;
	}
	
	public LinkedList<Edge> getPath(){
		return path;
	}

	public void dfsVisit(Graph.Vertex source,HashSet<Vertex> lv,boolean rev, boolean runOnAllEdges){
		/*
		 * DFSVisit(u)
		 * u.seen â†� true 
		 * u.dis â†� ++time 
		 * u.cno â†� cno 
		 * for each edge (u,v) going out of u 
		 *  do if ! v.seen then
		 *  v.parent â†� u 
		 *  DFSVisit(v)
		 * u.fin â†� ++time 
		 * u.top â†� topNum-- 
		 * decFinList.addFirst(u)
		 */	
	//	System.out.println("Is DMSTGRaph in dfs?");
//		System.out.println(g instanceof DMSTGraph);
//		System.out.println("dfs called for "+source);
//		source.iterator();
//		System.out.println("---end--");
		GraphUtilVertex u=getVertex(source);
		u.setSeen(true);
		source=g.gh.getVertex(source);
		++time;
		Iterator<Edge> eit=(rev)?source.reverseIterator():source.iterator();
		while(eit.hasNext()){

			Graph.Edge e=(Edge) eit.next();
			//System.out.println("edge "+e);
			DMSTEdge de=g.gh.getEdge(e);
			if(!runOnAllEdges && !de.isZeroEdge()){
				continue;
			}
			Graph.Vertex adjNode=g.gh.getVertex(e.otherEnd(source));
			GraphUtilVertex v=getVertex(adjNode);
			//System.out.println("vertex "+adjNode+"seen "+v.seen);
			if(!v.isSeen()){
				path.add(e);
				v.parent=source;
			//	System.out.println("inside call "+adjNode);
	            dfsVisit(adjNode,lv,rev, runOnAllEdges);
			}
			++time;
			topNum--;
		}
		lv.add(source);
		decFinList.addFirst(source);
	}
	}


/**
 * 1
*8 13
*1 2 5 
*2 3 3
*3 4 12 
*4 5 1 
*6 5 8 
*6 7 7 
*7 8 10 
*1 8 11
*8 2 6
*7 2 2
*2 6 13 
*3 6 9 
*5 3 4
**/