/**
 * Exempel på in- och utdatahantering för maxflödeslabben i kursen
 * ADK.
 *
 * Använder Kattio.java för in- och utläsning.
 * Se http://kattis.csc.kth.se/doc/javaio
 *
 * @author: Per Austrin
 */
import java.util.*;

class EdgeBefore {
    public int capacity;
    public int from, to;
    public Edge opposite;
}

class Edge {
    public int from, to;
    public int f,  c, cf; // f = flow, c = capacity, cf = rest of capacity
    public Edge opp; // edge with from and to flipped
}

class Solution {
    int v, s, t, sumFlow;
    ArrayList<Edge> posEdges;
}
class Flow {
    Kattio io;
    ArrayList<ArrayList<Edge>> flowGraph;
    int s, t, v, e;
    /*
      Find a new path from s to t.
      The return list's order determines visitation order.
      ret.size() == 0 implies no such path could be found.
    */
    int[] BFS(int start, int end) {
	Queue<Integer> paths = new ArrayDeque<Integer>(); // Q
        Integer startPath = start; // Where we start
	paths.add(startPath);
	int[] pathMap = new int[v+1];
	pathMap[start] = start;
	while(paths.size() > 0) {
	    Integer lastNode = paths.remove();;
	    if(lastNode == end) {
		return pathMap;
	    }
	    for(Edge e : flowGraph.get(lastNode)) {
		if(e.cf > 0 && pathMap[e.to] == 0) {
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

    void readGraph(ArrayList<EdgeBefore> g) {
	flowGraph = new ArrayList<ArrayList<Edge>>(this.v+1);
	for(int i = 0; i < this.v+1; i++) {
	    flowGraph.add(new ArrayList<Edge>());
	}
	for(EdgeBefore e : g) {
	    int from, to, cap;
	    Edge e1 = new Edge();
	    Edge e2 = new Edge();
	    from = e.from;
	    to = e.to;
	    cap = e.capacity;

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
    Solution giveOutput() {
	Solution sol = new Solution();
	sol.v = v;
	sol.s = s;
	sol.t = t;
	sol.sumFlow = sumFlow(s);
	sol.posEdges = getDemPosEdge();
	return sol; 
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
		if(e.f > 0 && (e.from != s) && (e.to != t)) {
		    ret.add(e);
		}
	    }
	}
	return ret;
    }
    Flow(ArrayList<EdgeBefore> g, int v, int e, int s, int t, Kattio io) {
	this.v = v;
	this.e = e;
	this.s = s;
	this.t = t;
	this.io = io;
	readGraph(g);
    }
    public void calcFlowGraph() {
	int[] path = BFS(s, t);
	while(path.length > 0) {
	    ArrayList<Edge> p = edgePath(path, t);
	    flowIt(p);
	    path = BFS(s, t);	    
	}
    }
}


public class BipFlow {
    Kattio io;
    // +1 since we skip elt 0 for the x-graph
    ArrayList<EdgeBefore> graph = new ArrayList<EdgeBefore>(5000+5000+1);
    int xsize, ysize, esize;
    ArrayList<EdgeBefore> solution = new ArrayList<EdgeBefore>(5000+5000+1);
    void readBipartiteGraph() {
	// Läs antal hörn och kanter
	xsize = io.getInt();
	ysize = io.getInt();
	int esize = io.getInt();
	int faucet = xsize+ysize+2;
	int sink = xsize+ysize+1;
	// Läs in kanterna
	for (int i = 0; i < esize; ++i) {
	    int x = io.getInt();
	    int y = io.getInt();
	    EdgeBefore fromXtoY; 
	    fromXtoY = new EdgeBefore();
	    fromXtoY.capacity = 1;
	    fromXtoY.from = x;
	    fromXtoY.to = y;
	    graph.add(fromXtoY);
	}
	for(int x = 1; x <= xsize; x++) {
	    EdgeBefore fromSinktoX = new EdgeBefore();
	    fromSinktoX.capacity = 1;
	    fromSinktoX.from = xsize+ysize+1;
	    fromSinktoX.to = x;
	    graph.add(fromSinktoX);
	}
	for(int y = xsize+1; y <= ysize+xsize; y++) {
	    EdgeBefore fromYtoFaucet = new EdgeBefore();
	    fromYtoFaucet.capacity = 1;
	    fromYtoFaucet.from = y;
	    fromYtoFaucet.to = xsize+ysize+2;
	    graph.add(fromYtoFaucet);
	}
    }

    void writeBipMatchSolution(Solution solution, int v, int s, int t) {
	int x = xsize, y = ysize, maxMatch = solution.posEdges.size();
	
	// Skriv ut antal hörn och storleken på matchningen
	io.println(x + " " + y);
	io.println(maxMatch);
	
	for (int i = 0; i < maxMatch; ++i) {
	    Edge e = solution.posEdges.get(i);
	    int a = e.from, b = e.to;
	    // Kant mellan a och b ingår i vår matchningslösning
	    if(!(a == s || a == t || b == s || b == t)) {
		io.println(a + " " + b);
	    }
	}
	io.flush();
    }
    
    BipFlow() {
	io = new Kattio(System.in, System.out);
	
	readBipartiteGraph();
	int v = xsize+ysize+2, e = graph.size(), s = xsize+ysize+1, t = xsize+ysize+2;

	Flow f = new Flow(graph,v,e,s,t,io);
	f.calcFlowGraph();
	writeBipMatchSolution(f.giveOutput(),v,s,t);

	// debugutskrift
	//System.err.println("Bipred avslutar\n");

	// Kom ihåg att stänga ner Kattio-klassen
	io.close();
    }
    
    public static void main(String args[]) {
	new BipFlow();
    }
}

