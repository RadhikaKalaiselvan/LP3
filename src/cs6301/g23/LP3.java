// Starter code for LP3
// Do not rename this file or move it away from cs6301/g??

// change following line to your group number
package cs6301.g23;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import cs6301.g23.Graph.Vertex;
import cs6301.g23.Graph.Edge;
import cs6301.g23.Timer;

public class LP3 {
    static int VERBOSE = 0;
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in;
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        } else {
            in = new Scanner(System.in);
        }
	if(args.length > 1) {
            VERBOSE = Integer.parseInt(args[1]);
        }

	int start = in.nextInt();  // root node of the MST
        Graph g = Graph.readDirectedGraph(in);
	Vertex startVertex = g.getVertex(start);
	List<Edge> dmst = new ArrayList<>();

        Timer timer = new Timer();
	int wmst = directedMST(g, startVertex, dmst);
        timer.end();

	System.out.println(wmst);
        if(VERBOSE > 0) {
	    System.out.println("_________________________");
            for(Edge e: dmst) {
                System.out.print(e);
            }
	    System.out.println();
	    System.out.println("_________________________");
        }
        System.out.println(timer);
    }

    /** TO DO: List dmst is an empty list. When your algorithm finishes,
     *  it should have the edges of the directed MST of g rooted at the
     *  start vertex.  Edges must be ordered based on the vertex into
     *  which it goes, e.g., {(7,1),(7,2),null,(2,4),(3,5),(5,6),(3,7)}.
     *  In this example, 3 is the start vertex and has no incoming edges.
     *  So, the list has a null corresponding to Vertex 3.
     *  The function should return the total weight of the MST it found.
     */  
    public static int directedMST(Graph g, Vertex start, List<Edge> dmst) {
    	DMSTGraph dg=new DMSTGraph(g);
    	 dg.directedMST(dg,start,dmst);
    	 
    	 dg.disableVertex(g.getVertex(1));
    	 
    	 
    	//dg.disableVertex(g.getVertex(1));
    	 Vertex n=dg.createNewVertex(null);
    	 Edge x=null;
    	 int i=0;
    	 for(Vertex v: dg){
    		 for(Edge e:v){
    			 if(i==0){
    				 x=e;
    				 dg.disableEdge(e);
    				 System.out.println(" after disable edge .. "+e);
    				 dg.gh.getEdge(e).setTempWeight(10);
    			     dg.createNewEdge(v,n,e);
    				 i++;
    			 }
    			 break;
    		 }
    	 }
    	
    	 
    	 
    	 dg.enableEdge(x);
    	
    	
    	
    	/* System.out.println(" after disable.. 3,1");
    	 dg.directedMST(dg,start,dmst);
    	 dg.disableVertex(g.getVertex(2));
    	 dg.disableVertex(g.getVertex(4));
    	 System.out.println(" after disable.. all");*/
    	 dg.directedMST(dg,start,dmst);
    	 
    	 //check strongly connected components
    	 GraphUtil gu=new GraphUtil(dg);
    	 ArrayList<LinkedList<Vertex>> comp=gu.stronglyConnectedComponents();
    	 int k=0;
    	 for(LinkedList<Vertex> lv: comp){
    		System.out.println("key "+k);
    		for(Vertex v:lv){
    			System.out.println(" "+v);
    		}
    		k++;
    	 }
    	 return 0;	
    }
}