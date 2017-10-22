package cs6301.g23;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cs6301.g23.DMSTGraph.DMSTEdge;
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
    ArrayList<LinkedList<Vertex>> compList;
	GraphUtil(Graph g){
		super(g);
		this.g=(DMSTGraph)g;
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
		System.out.println(" strongly ");
		LinkedList<Vertex> result=new LinkedList<Vertex>(dfs(this.g.iterator(),false));
		this.decFinList.clear();
		reinitialize();
		this.compList.clear();
		dfs(result.iterator(),true);
		return this.compList;
	}
	
	public LinkedList<Graph.Vertex> dfs(Iterator<Vertex> it,boolean rev){
		topNum=g.size();
		time=0;
		cno=0;
		this.decFinList.clear();
		while(it.hasNext()){
			Vertex uVert=it.next();
			GraphUtilVertex u=getVertex(uVert);
			//System.out.println(" vertex in util "+uVert);
			if(!u.seen){
				cno++;
				LinkedList<Vertex> lv=new LinkedList<Vertex>();
				DfsVisit(uVert,lv,rev);
				compList.add(lv);
			}
		}
		return decFinList;
	}

	public void DfsVisit(Graph.Vertex source,LinkedList<Vertex> lv,boolean rev){
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
		Iterator<Edge> eit=(rev)?source.reverseIterator():source.iterator();
		while(eit.hasNext()){
			Graph.Edge e=(Edge) eit.next();
			DMSTEdge de=g.gh.getEdge(e);
			if(!de.isZeroEdge()){
				continue;
			}
			Graph.Vertex adjNode=e.otherEnd(source);
			GraphUtilVertex v=getVertex(adjNode);
			if(!v.isSeen()){
				v.parent=source;
	            DfsVisit(adjNode,lv,rev);
			}
			++time;
			topNum--;
		}
		decFinList.addFirst(source);
	}
	}
