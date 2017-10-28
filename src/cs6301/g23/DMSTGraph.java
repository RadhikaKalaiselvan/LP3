package cs6301.g23;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cs6301.g23.Graph.Edge;
import cs6301.g23.Graph.Vertex;


public class DMSTGraph extends Graph {
	GraphHash<DMSTVertex,DMSTEdge> gh=null;
	DMSTVertex[] dv;
	Vertex lastSuperNode;
	int vertexCount=0;
	int enabledVertexCount =0;
	
	public DMSTGraph(Graph g) {
		super(g);
		this.vertexCount=g.n;
		this.gh=new GraphHash<DMSTVertex,DMSTEdge>(g);
		dv = new DMSTVertex[2*g.size()];  // Extra space is allocated in array for nodes to be added later
		for(Vertex u: g) {
			DMSTVertex dvertx=new DMSTVertex(u);
			dv[u.getName()] = dvertx;
			gh.putVertex(u,dvertx);
		}
		enabledVertexCount = g.size();
		for(Vertex u: g) {
			for(Edge e: u) {
				Vertex v = e.otherEnd(u);
				DMSTVertex x1 = getVertex(u);
				DMSTVertex x2 = getVertex(v);
				DMSTEdge dedge=new DMSTEdge(x1, x2, e.weight);
				x1.dadj.add(e);
				gh.putEdge(e,dedge);
			}
		}
	}

	DMSTVertex getVertex(Vertex u) {
		return Vertex.getVertex(dv, u);
	}
	
	public void disableVertex(Vertex u){
		DMSTVertex dmstv=gh.getVertex(u);
		dmstv.disabled=true;
		enabledVertexCount--;
	}

	public void enableVertex(Vertex u){
		DMSTVertex dmstv=gh.getVertex(u);
		dmstv.disabled=false;
		enabledVertexCount++;
	}


	public void disableEdge(Edge u){
		DMSTEdge dmste=gh.getEdge(u);
		dmste.disabled=true;
	}

	public void enableEdge(Edge u){
		DMSTEdge dmste=gh.getEdge(u);
		dmste.disabled=false;
	}
	public Vertex getLastSuperNode(){
		return lastSuperNode;
	}
	public Vertex createNewVertex(HashSet<Vertex> comp){
		Vertex v=new Vertex(vertexCount++);
		DMSTVertex dvertx=new DMSTVertex(v);
		dvertx.setComp(comp);
		dvertx.isSuperNode = true;
		dv[v.getName()] = dvertx;
		gh.putVertex(v,dvertx);
		enabledVertexCount++;
		lastSuperNode=v;
		return v;
	}
	
   public Edge createNewEdge(Vertex source,Vertex dest,Edge originalEdge){
	    DMSTEdge dmstOrgEdg=gh.getEdge(originalEdge);
	    Edge e=new Edge(source,dest,dmstOrgEdg.tempWeight);
	    DMSTVertex x1 = getVertex(source);
		DMSTVertex x2 = getVertex(dest);
		DMSTEdge dedge=new DMSTEdge(x1, x2,dmstOrgEdg.tempWeight);
		dedge.originalEdge=originalEdge;
		x1.dadj.add(e);
		x2.revAdj.add(e);
		gh.putEdge(e,dedge);
		return e;
   }
   
	class DMSTVertex extends Vertex {
		boolean disabled;
		List<Edge> dadj;
		int delta;
		Edge mstEdge;
		boolean isSuperNode;
		boolean foundIncoming;
		HashSet<Vertex> comp;
		
		
		public DMSTVertex(Vertex u) {
			super(u);
			foundIncoming = false;
			isSuperNode = false;
			mstEdge = null;
			dadj=new LinkedList<Edge>();
		}
		public List<Edge> getDadj() {
			return dadj;
		}

		public void setDadj(List<Edge> dadj) {
			this.dadj = dadj;
		}

		public int getDelta() {
			return delta;
		}

		public void setDelta(int delta) {
			this.delta = delta;
		}

		public HashSet<Vertex> getComp() {
			return comp;
		}

		public void setComp(HashSet<Vertex> comp2) {
			this.comp = comp2;
		}

		@Override
		public Iterator<Edge> iterator() { 
			//System.out.println("DMST edge it");
			return new DMSTVertexIterator(this,false); }
		
		public Iterator<Edge> reverseIterator() { return new DMSTVertexIterator(this,true); }


		class DMSTVertexIterator implements Iterator<Edge> {
			Edge cur;
			Iterator<Edge> it;
			boolean ready;

			DMSTVertexIterator(DMSTVertex u,boolean isrev) {
				if(isrev){
					this.it = u.revAdj.iterator();
				}else {
					this.it = u.dadj.iterator();
				}
				ready = false;
			}

			public boolean hasNext() {
//				System.out.println("DMST it");
				if(ready) { 
					return true; 
				}
				if(!it.hasNext()) { 
					return false; 
				}
				cur = it.next();	
				DMSTEdge de=gh.getEdge(cur);
				while(de.isDisabled() && it.hasNext()) {
					cur = it.next();
					de=gh.getEdge(cur);
				}
				ready = true; //check repeated call to has next on set of only disabled edges will return true.
				return !de.isDisabled();
			}

			public Edge next() {
				if(!ready) {
					if(!hasNext()) {
						throw new java.util.NoSuchElementException();
					}
				}
				ready = false;
				return cur;
			}

			public void remove() {
				throw new java.lang.UnsupportedOperationException();
			}
		}

		public boolean isDisabled() {
			return this.disabled;
		}  
	}
	class DMSTEdge extends Edge{
		public int tempWeight;
		public boolean isInPath;
		
		Edge originalEdge;
		boolean disabled;
		public DMSTEdge(Edge e) {
			super(e);
			disabled = false;
			isInPath=false;
		}
		DMSTEdge(Vertex from, Vertex to, int weight) {
			super(from, to, weight);
			tempWeight=weight;
			disabled = false;
		}

		public int getTempWeight() {
			return tempWeight;
		}
		public void setTempWeight(int tempWeight) {
			this.tempWeight = tempWeight;
		}
		
		boolean isDisabled() {
			Vertex xfrom = (Vertex) from;
			Vertex xto = (Vertex) to;
			return disabled || gh.getVertex(xfrom).disabled || gh.getVertex(xto).disabled;
		}

		boolean isZeroEdge(){
			return (this.tempWeight==0)?true:false;
		}
	}
	
	public void printKeySet(){
		for(Vertex v:this){
			System.out.println("vertex : "+v+", disabled:"+gh.getVertex(v).disabled);
			for(Edge e:v){
				System.out.println("e :      "+e+" "+e.weight);   
			}
		}
	}

	@Override
	public Iterator<Vertex> iterator() { return new DMSTGraphIterator(this); }

	
	//add change to iterator all new nodes as well.
	class DMSTGraphIterator implements Iterator<Vertex> {
		Iterator<Vertex> it;
		Vertex cur;

		DMSTGraphIterator(DMSTGraph xg) {
			this.it = new ArrayIterator<Vertex>(xg.dv, 0, xg.vertexCount-1);  // Iterate over existing elements only
		}


		public boolean hasNext() {
			if(!it.hasNext()) { return false; }
			cur = it.next();
			DMSTVertex dmstv=gh.getVertex(cur);
			while(dmstv.isDisabled() && it.hasNext()) {
				cur = it.next();
				dmstv=gh.getVertex(cur);
			}
			return !dmstv.isDisabled();
		}

		public Vertex next() {
			return cur;
		}

		public void remove() {
		}
	}
	
	
}

