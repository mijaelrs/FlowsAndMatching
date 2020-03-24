import java.util.*;
import java.io.*;

import java.util.Collections;

class Edge {
    public int from, to;
    public int f,  c, cf; // f = flow, c = capacity, cf = rest of capacity
    public Edge opp; // edge with from and to flipped
}

public class Flow {
    Kattio io;
    ArrayList<ArrayList<Edge>> flowGraph;
    int s, t, v, e;
    /*
      Find a new path from s to t.
      The return list's order determines visitation order.
      ret.size() == 0 implies no such path could be found.
    */
    int[] BFS(int start, int end) throws InterruptedException  {
	Queue<Integer> paths = new ArrayDeque<Integer>(); // Q
        Integer startPath = start; // Where we start
	paths.add(startPath);
	int[] pathMap = new int[v+1];
	pathMap[start] = start;
	while(paths.size() > 0) {
	    Integer lastNode = paths.remove();
	    //System.out.println(Arrays.toString(pathMap));
	    //Thread.sleep(1000);
	    if(lastNode == end) {
		return pathMap;
	    }
	    for(Edge e : flowGraph.get(lastNode)) {
		//System.out.println("EDGE: " + "(" + e.from + ", " + e.to + ")" + " " + e.cf);
		if(e.cf > 0 && pathMap[e.to] == 0) {
		    //Thread.sleep(100);
		    pathMap[e.to] = lastNode;
		    paths.add(e.to);
		}
	    }
	}
	return new int[0];
    }
    ArrayList<Edge> edgePath(int[] pathMap, int end) {
	ArrayList<Edge> ret = new ArrayList<Edge>();
	int from, to, r;
	to = end;
	from = pathMap[end];
	while(to != pathMap[to]) {
	    from = pathMap[to];
	    ArrayList<Edge> possibleEdges = flowGraph.get(from);
	    for(Edge e : possibleEdges) {
		if(e.to == to && e.cf > 0) {
		    ret.add(e);
		    to = from;
		    break; // outerloop;
		}
	    }
	}
	return ret;
    }
    /*
      Perform the flow computation of:
      1. Find max-flow in the list
      2. Reduce capacity of the edges and
      increase capacity of their opposing edges by max-flow
      This computation is performed directly in the flowGraph.
    */
    void flowIt(ArrayList<Edge> path) {
	//    r:=min(cf[u,v]: (u,v) ingår i p) 
	int r = 99999999;
	for(Edge e : path) {
	    if(e.cf < r) {
		r = e.cf;
	    }
	}
	/*
	  for varje kant (u,v) i p do 
	  f[u,v]:=f[u,v]+r;
	  f[v,u]:= -f[u,v];
	  cf[u,v]:=c[u,v] - f[u,v];
	  cf[v,u]:=c[v,u] - f[v,u];
	*/
	for(Edge e : path) {
	    e.f = e.f + r;
	    e.opp.f = -e.f;
	    e.cf = e.c - e.f;
	    e.opp.cf = e.opp.c - e.opp.f;
	}
    }

    /*
      Fill the graph from stdin.
    */
    void readGraph() {
	v = io.getInt();
	s = io.getInt();
	t = io.getInt();
	e = io.getInt();
	flowGraph = new ArrayList<ArrayList<Edge>>(v+1);
	for(int i = 0; i < v+1; i++) {
	    flowGraph.add(new ArrayList<Edge>());
	}
	for(int i = 0; i < e; i++) {
	    int from, to, cap;
	    Edge e1 = new Edge();
	    Edge e2 = new Edge();
	    from = io.getInt();
	    to = io.getInt();
	    cap = io.getInt();
	    
	    e1.from = from;
	    e1.to = to;
	    
	    e1.c = cap;
	    e1.cf = e1.c;
	    e1.f = 0;

	    
	    e2.from = to;
	    e2.to = from;
	    
	    e2.c = 0;
	    e2.f = 0;
	    e2.cf = 0;

	    e1.opp = e2;
	    e2.opp = e1;

	    ArrayList<Edge> node1 = flowGraph.get(from);
	    ArrayList<Edge> node2 = flowGraph.get(to);
	    node1.add(e1);
	    node2.add(e2);
	}
    }
    void writeOutput() {
	io.println(v);
	io.println(s + " " + t + " " + sumFlow(s));
	ArrayList<Edge> positiveEdges = getDemPosEdge();
	io.println(positiveEdges.size());
	for(Edge e : positiveEdges) {
	    io.println(e.from + " " + e.to + " " + e.f);
	}
	io.flush();
    }
    int sumFlow(int s) {
	int flow = 0;
	for(Edge e : flowGraph.get(s)) {
	    if(e.f > 0) {
		flow += e.f;
	    }
	}
	return flow;
    }
    ArrayList<Edge> getDemPosEdge() {
	ArrayList<Edge> ret = new ArrayList<Edge>();
	for(ArrayList<Edge> le : flowGraph) {
	    for(Edge e : le) {
		if(e.f > 0) {
		    ret.add(e);
		}
	    }
	}
	return ret;
    }
    void printGraph(ArrayList<ArrayList<Edge>> p) {
	for(ArrayList<Edge> le : p) {
	    for(Edge e : le) {
		System.out.println("EDGE");
		System.out.print(e.from + ", ");
		System.out.print(e.to + ", ");
		System.out.print(e.f);
		System.out.print("/");
		System.out.print(e.c);
		System.out.println();
	    }
	}
    }
    Flow() throws InterruptedException {
	io = new Kattio(System.in, System.out);
	readGraph();
	//printGraph(flowGraph);
	//Thread.sleep(1000);
	int[] path = BFS(s, t);

	
        //Thread.sleep(1000);
	while(path.length > 0) {
	    ArrayList<Edge> p = edgePath(path, t);
	    //System.out.print("{" );
	    //for(Edge e : p) {
		//	System.out.print("("+ e.from + ", " + e.to + ")"+", ");
		//}
	    //System.out.println("}" );
	    flowIt(p);
	    //printGraph(flowGraph);
	    path = BFS(s, t);
	    //Thread.sleep(1000);
	    
	}
	writeOutput();
    }

    public  static void main(String[] args) throws Exception {
	try {
	    new Flow();
	}
	catch(Exception e) {
	    System.out.println(e);
	    throw e;
	}
    }
}
