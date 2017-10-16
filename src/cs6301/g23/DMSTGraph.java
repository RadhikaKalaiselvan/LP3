package cs6301.g23;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class DMSTGraph extends Graph {
	GraphHash<DMSTVertex,DMSTEdge> gh=null;
	DMSTVertex[] dv;
	int vertexCount=0;
	
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
	}

	public void enableVertex(Vertex u){
		DMSTVertex dmstv=gh.getVertex(u);
		dmstv.disabled=false;
	}


	public void disableEdge(Edge u){
		DMSTEdge dmste=gh.getEdge(u);
		dmste.disabled=true;
	}

	public void enableEdge(Edge u){
		DMSTEdge dmste=gh.getEdge(u);
		dmste.disabled=false;
	}
	
	public Vertex createNewVertex(List<Vertex> comp){
		Vertex v=new Vertex(vertexCount++);
		DMSTVertex dvertx=new DMSTVertex(v);
		dvertx.setComp(comp);
		dv[v.getName()] = dvertx;
		gh.putVertex(v,dvertx);
		return v;
	}
	
   public Edge createNewEdge(Vertex source,Vertex dest,Edge originalEdge){
	   System.out.println("create edge called "+gh.edgeMap.size());
	    DMSTEdge dmstOrgEdg=gh.getEdge(originalEdge);
	    //System.out.println("New edge weight "+dmstOrgEdg.tempWeight);
	    Edge e=new Edge(source,dest,dmstOrgEdg.tempWeight);
	    DMSTVertex x1 = getVertex(source);
		DMSTVertex x2 = getVertex(dest);
		DMSTEdge dedge=new DMSTEdge(x1, x2,dmstOrgEdg.tempWeight);
		x1.dadj.add(e);
		gh.putEdge(e,dedge);
		// System.out.println("create edge end "+gh.edgeMap.size());
		return e;
   }
   
	class DMSTVertex extends Vertex {
		boolean disabled;
		List<Edge> dadj;
		int delta;
		List<Vertex> comp;
		public DMSTVertex(Vertex u) {
			super(u);
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

		public List<Vertex> getComp() {
			return comp;
		}

		public void setComp(List<Vertex> comp) {
			this.comp = comp;
		}

		@Override
		public Iterator<Edge> iterator() { return new DMSTVertexIterator(this); }

		class DMSTVertexIterator implements Iterator<Edge> {
			Edge cur;
			Iterator<Edge> it;
			boolean ready;

			DMSTVertexIterator(DMSTVertex u) {
				this.it = u.dadj.iterator();
				ready = false;
			}

			public boolean hasNext() {
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
		
		Edge originalEdge;
		boolean disabled;
		public DMSTEdge(Edge e) {
			super(e);
			disabled = false;
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
			return (this.weight==0)?true:false;
		}
	}

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
	
	public int directedMST(DMSTGraph g, Vertex start, List<Edge> dmst){
		for(Vertex v:g){
			System.out.println("Vertex = "+v);
			for(Edge e:v){
				System.out.println("  edge="+e);
			}
		}
		return 0;
	}
	
}

