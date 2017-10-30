package cs6301.g23;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

	public int mst(List<Edge> dmst){
		DMSTVertex dVertex;
		
		int tempWeight=0;
		
		while(true){
//			g.printW();
//			System.out.println("weight calculation start");
			for(Vertex v:g){
				
				dVertex = g.getVertex(v);
//				System.out.println(" "+getMinWeight(v)+" "+dVertex.minWeight);
//				int min = getMinWeight(v);
//				dVertex.setDelta(min);
				Iterator<Edge> edgeIt = v.reverseIterator();
                  int min=Integer.MAX_VALUE;
				while(edgeIt.hasNext()){
					Edge e = edgeIt.next();
					tempWeight = g.gh.getEdge(e).getTempWeight() - dVertex.minWeight;
					
					
					if(tempWeight<min){
						min=tempWeight;
						}
					if(tempWeight==0){
						dVertex.hasZeroEdge=true;
					}
					g.gh.getEdge(e).setTempWeight(tempWeight);
					//System.out.println("Edge "+e+" weight "+tempWeight);
				}
				dVertex.minWeight=min;
//				System.out.println("vertex "+v+" min wt "+dVertex.minWeight);
			
			}
//			g.printW();
//			System.out.println("weight calculation end");
//			System.out.println("strongly connected start");
			ArrayList<HashSet<Vertex>> scc = gUtil.stronglyConnectedComponents();
			int k=0;
//			System.out.println("strongly connected end");
//			System.out.println("shrink called");
			if(scc.size()>1){
//				for(HashSet<Vertex> lv: scc){
//					System.out.println("key "+k);
//					for(Vertex v:lv){
//						System.out.println(" "+v);
//					}
//					k++;
//				}
				for(HashSet<Vertex> l:scc){
					if(l.size()>1){
						System.out.println("shrink call");
						shrinkComponent(l);
					}
				}
			}
//			System.out.println("shrink call ended");
			HashSet<Graph.Vertex> dfsVisited = new HashSet<Graph.Vertex>();
			gUtil.reinitialize();
			if(g.source==null){
				g.source=getSource();
			}
			gUtil.dfsVisit(g.source, dfsVisited,	false,false);
			if(dfsVisited.size()==g.enabledVertexCount){  //if all enabled nodes are reachable
				System.out.println("scc is 1");
				break;
			}
		}
//		System.out.println("Last super node "+g.getLastSuperNode());
//		g.printKeySet();

		for(Edge e:gUtil.getPath()){
			DMSTVertex dv=g.gh.getVertex(e.to);
			dv.foundIncoming=true;
			dv.mstEdge=e;
//			System.out.println(" e :"+e.weight+" mstedge "+dv.mstEdge.weight);
			g.gh.getEdge(e).isInPath=true;
		}
//		System.out.println("count :"+g.enabledVertexCount);
//		Iterator revi=g.reverseIterator();
//		while(revi.hasNext()){
//			System.out.println(" rn "+revi.next());
//		}
		expandGraph();

//		g.printKeySet();
		return getPath(dmst);
	}

	public void shrinkComponent(HashSet<Vertex> shrinkComp){
//		System.out.println("new vertex create begin");
		Vertex v = g.createNewVertex(shrinkComp);
//		System.out.println("new vertex created");
//		System.out.println("new vertex added");
		gUtil.addVertex(v);
//		System.out.println("new vertex added");
		HashMap<Vertex, Edge> source_edge = new HashMap<Vertex, Edge>();
		HashMap<Vertex, Edge> dest_edge = new HashMap<Vertex, Edge>();
//		System.out.println("shrinking begins");
		Iterator<Vertex> compIt = shrinkComp.iterator();
		while(compIt.hasNext()){
			Vertex t = compIt.next();
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
//		System.out.println("shrinking ends");

		for(Vertex vert:dest_edge.keySet()){
			g.createNewEdge(v, vert, dest_edge.get(vert));
			//			System.out.println("new edge "+newEdge+" weight "+g.gh.getEdge(newEdge).getTempWeight());
		}
		for(Vertex vert:source_edge.keySet()){
			g.createNewEdge(vert, v, source_edge.get(vert));
			//			System.out.println("new edge "+newEdge+" weight "+g.gh.getEdge(newEdge).getTempWeight());
		}
//		System.out.println("Created"+v);
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
//		System.out.println("source "+source);
		if(g.source==null){
			g.source=source;
		}
		return source;
	}

	public Vertex takeGraphImage(HashSet<Vertex> hv){
		Vertex source=null;
		graphImage.clear();
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
//			System.out.println("toggle : Vertex "+v+" disable="+disable);
			dVert.disabled=disable;
		}
	}

	public void disableAllEdgesOfComp(HashSet<Vertex> hv){
		for(Vertex v:hv)
		{
			for(Edge e:v.adj){
				if(!(hv.contains(e.to) && hv.contains(e.from))){
//					System.out.println("disabled "+e);
					g.gh.getEdge(e).disabled=true;
				}
			}
			for(Edge ed:v.revAdj){
				if(!(hv.contains(ed.to) && hv.contains(ed.from))){
					g.gh.getEdge(ed).disabled=true;
//					System.out.println("disabled "+ed);
				}
			}
		}
	}

	public void expandGraph(){
		Iterator<Vertex> revi=g.reverseIterator();
		while(revi.hasNext()){
			Vertex superNode=revi.next();
			if(g.gh.getVertex(superNode).isSuperNode){
				expand(superNode);
			} else {
				break;
			}
		}		
	}

	void expand(Vertex superNode){

//		System.out.println("Super node "+superNode);
		DMSTVertex dmstSuperNode=g.gh.getVertex(superNode);
		HashSet<Vertex> hv=dmstSuperNode.getComp();
		Vertex sNode=g.gh.getVertex(superNode);
		for(Edge e:sNode){
			DMSTEdge deEdge=g.gh.getEdge(e);
			if(deEdge.isInPath){
				Edge orig=deEdge.originalEdge;
//				System.out.println("edge:"+e+" edge image "+orig+" set isInpath");
				g.enableEdge(orig);
				g.gh.getEdge(orig).isInPath=true;
				DMSTVertex u=g.gh.getVertex(orig.to);
				u.foundIncoming=true;
				u.mstEdge=orig;
//				System.out.println(" Set mst Edge e :"+orig.weight+" mstedge "+u.mstEdge.weight);
			}
		}
		Iterator<Edge> ite=superNode.reverseIterator();
		while(ite.hasNext()){
			Edge e=ite.next();
			DMSTEdge deEdge=g.gh.getEdge(e);
			if(deEdge.isInPath){
				Edge orig=deEdge.originalEdge;
//				System.out.println("edge:"+e+" edge image "+orig+" set isInpath");
				g.enableEdge(orig);
				g.gh.getEdge(orig).isInPath=true;
				DMSTVertex u=g.gh.getVertex(orig.to);
				u.foundIncoming=true;
				u.mstEdge=orig;
//				System.out.println(" Set mst Edge e :"+orig.weight+" mstedge "+u.mstEdge.weight);
			}
		}

		g.disableVertex(superNode);
//		System.out.println("Super Node Diabled Node "+superNode);

		for(Vertex ve:hv){
			DMSTVertex dv=g.gh.getVertex(ve);
//			System.out.println("Next SuperNode Enable vertex ="+ve);
			g.enableVertex(dv);
		}

		Vertex source=takeGraphImage(hv);
//		System.out.println("source for dfs "+source);
		toggleNodes(true);
		gUtil.reinitialize();
		gUtil.dfsVisit(source,new HashSet<Vertex>(),false,false);
		HashSet<Edge> path=gUtil.getPath();
//		System.out.println("------------------Path------------------");
//		for(Edge p:path){
//			System.out.println(" "+p);
//		}
//		System.out.println("----------------------------------------");
		for(Vertex v:hv)
		{
			Vertex ver=g.gh.getVertex(v);
//			System.out.println("        vertex :"+v);
			Iterator<Edge> eit=ver.iterator();
			while(eit.hasNext()){
				Edge e=eit.next();
//				System.out.println("second part : edge ="+e);
				if(path.contains(e)){
//					System.out.println("after dfs : edge:"+e+" edge image  set isInpath");
					DMSTVertex dv=g.gh.getVertex(e.to);
					dv.foundIncoming=true;
					dv.mstEdge=e;
//					System.out.println(" Set mst Edge e after dfs :"+e.weight+" mstedge "+dv.mstEdge.weight);
					g.gh.getEdge(e).isInPath=true;
				} else {
					if(!g.gh.getEdge(e).isInPath){
//						System.out.println("disabled "+e);
						g.disableEdge(e);
					}
				}
			}
		}
		toggleNodes(false);
	}


	int getPath(List<Edge> dmst){
		
		int mst=0;
//		gUtil.reinitialize();
//		System.out.println("source "+g.source);
//		gUtil.dfsVisit(g.source,new HashSet<Vertex>(),false,false);
//		HashSet<Edge> path=gUtil.getPath();
//		System.out.println("size "+path.size());
//		for(Edge e:path){
//			System.out.println(" *");
//			DMSTVertex dv=g.gh.getVertex(e.to);
//			dv.mstEdge=e;
//			mst+=dv.mstEdge.weight;
//		}
		for(Vertex v:g){
			DMSTVertex dv=g.gh.getVertex(v);
			if(!dv.isSuperNode){
				Edge finalEdge=dv.mstEdge;
				if(finalEdge!=null){
				mst+=dv.mstEdge.weight;
				}
				 dmst.add(finalEdge);
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