package cs6301.g23;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cs6301.g23.Graph.Edge;
import cs6301.g23.Graph.Vertex;
import cs6301.g23.GraphUtil.*;

public class GraphUtil extends GraphHash<GraphUtilVertex,Boolean>{
	Graph g;
	public static int time;
    public static int cno;
    public static int topNum;
    int graphSize;
    LinkedList<Graph.Vertex> decFinList;
    ArrayList<LinkedList<Vertex>> compList;
	GraphUtil(Graph g){
		super(g);
		this.g=g;
		graphSize=g.n;
		 decFinList=new LinkedList<Graph.Vertex>();
		 this.compList=new ArrayList<LinkedList<Vertex>>();
		 for(Graph.Vertex u: g) {
			    putVertex(u, new GraphUtilVertex(u));
			}
	}
	static class GraphUtilVertex {
		boolean seen;
		Vertex parent;
		GraphUtilVertex(Vertex u) {
		    seen = false;
		    parent = null;
		}
		
		void reinitialize() {
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
	
	 boolean seen(Vertex u) {
			return getVertex(u).seen;
		    }
	 
	 Graph.Vertex getParent(Vertex u) {
			return getVertex(u).parent;
		    }
	 	 
	 void reinitialize() {
			for(Vertex u: g) {
			    GraphUtilVertex bu = getVertex(u);
			    bu.reinitialize();
			}
		    }
	 
	public ArrayList<LinkedList<Vertex>> stronglyConnectedComponents() { 
		System.out.println(" size "+vertexMap.size());
		LinkedList<Vertex> result=new LinkedList<Vertex>(dfs(this.g.iterator()));
		this.decFinList.clear();
		reinitialize();
		reverseEdges(g);
		this.compList.clear();
		 dfs(result.iterator());
		reverseEdges(g);
		return this.compList;
	}
	
	public LinkedList<Graph.Vertex> dfs(Iterator<Vertex> it){
		topNum=g.size();
		time=0;
		cno=0;
		this.decFinList.clear();
		while(it.hasNext()){
			Vertex uVert=it.next();
			GraphUtilVertex u=getVertex(uVert);
			if(!u.seen){
				cno++;
				LinkedList<Vertex> lv=new LinkedList<Vertex>();
				DfsVisit(uVert,lv);
				compList.add(lv);
			}
		}
		return decFinList;
	}

	public void DfsVisit(Graph.Vertex source,LinkedList<Vertex> lv){
		/*
		 * DFSVisit(u)
		 * u.seen ← true 
		 * u.dis ← ++time 
		 * u.cno ← cno 
		 * for each edge (u,v) going out of u 
		 *  do if ! v.seen then
		 *  v.parent ← u 
		 *  DFSVisit(v)
		 * u.fin ← ++time 
		 * u.top ← topNum-- 
		 * decFinList.addFirst(u)
		 */	
		
		GraphUtilVertex u=getVertex(source);
		u.setSeen(true);
		lv.add(source);
		++time;
		for(Graph.Edge e: source) {
			System.out.println("edge "+e);
			Graph.Vertex adjNode=e.otherEnd(source);
			//System.out.println("other edge "+adjNode); 
			GraphUtilVertex v=getVertex(adjNode);
			if(!v.isSeen()){
				v.parent=source;
	            DfsVisit(adjNode,lv);
			}
			++time;
			topNum--;
		}
		decFinList.addFirst(source);
	}
		

	
	public void reverseEdges(Graph g){
		for(Vertex u: g) {
			List<Edge> tmp = u.adj;
			u.adj = u.revAdj;
			u.revAdj = tmp;
		    }
	}

	}
