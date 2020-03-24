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

class Edge {
    public int capacity;
    public int from, to;
    public Edge opposite;
}

public class BipRed {
    Kattio io;
    // +1 since we skip elt 0 for the x-graph
    ArrayList<Edge> graph = new ArrayList<Edge>(5000+5000+1);
    int xsize, ysize, esize;
    
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
	    Edge fromXtoY; 
	    fromXtoY = new Edge();
	    fromXtoY.capacity = 1;
	    fromXtoY.from = x;
	    fromXtoY.to = y;
	    graph.add(fromXtoY);
	}
	for(int x = 1; x <= xsize; x++) {
	    Edge fromSinktoX = new Edge();
	    fromSinktoX.capacity = 1;
	    fromSinktoX.from = xsize+ysize+1;
	    fromSinktoX.to = x;
	    graph.add(fromSinktoX);
	}
	for(int y = xsize+1; y <= ysize+xsize; y++) {
	    Edge fromYtoFaucet = new Edge();
	    fromYtoFaucet.capacity = 1;
	    fromYtoFaucet.from = y;
	    fromYtoFaucet.to = xsize+ysize+2;
	    graph.add(fromYtoFaucet);
	}
    }
    void writeFlowGraph() {
	int v = xsize+ysize+2, e = graph.size(), s = xsize+ysize+1, t = xsize+ysize+2;
	
	// Skriv ut antal hörn och kanter samt källa och sänka
	io.println(v);
	io.println(s + " " + t);
	io.println(e);
	for (Edge ed : graph) {
	    // Kant från a till b med kapacitet c
	    io.println(ed.from + " " + ed.to + " " + ed.capacity);
	}
	// Var noggrann med att flusha utdata när flödesgrafen skrivits ut!
	io.flush();
	
	// Debugutskrift
	//System.err.println("Skickade iväg flödesgrafen");
    }
    

    ArrayList<Edge> solution =  new ArrayList<Edge>();
    void readMaxFlowSolution() {
	// Läs in antal hörn, kanter, källa, sänka, och totalt flöde
	// (Antal hörn, källa och sänka borde vara samma som vi i grafen vi
	// skickade 
	int v = io.getInt();
	int s = io.getInt();
	int t = io.getInt();
	int totflow = io.getInt();
	int e = io.getInt();

	// Ifall a eller b är flöde eller sänka så strunta i att samla den kanten
	for (int i = 0; i < e; ++i) {
	    // Flöde f från a till b
	    int a = io.getInt();
	    int b = io.getInt();
	    int f = io.getInt();

	    if(!(a == s || a == t || b == s || b == t)) {
		Edge edge = new Edge();
		edge.from = a;
		edge.to = b;
		solution.add(edge);
	    }
	}
    }
    void writeBipMatchSolution() {
	int x = xsize, y = ysize, maxMatch = solution.size();
	
	// Skriv ut antal hörn och storleken på matchningen
	io.println(x + " " + y);
	io.println(maxMatch);
	
	for (int i = 0; i < maxMatch; ++i) {
	    Edge e = solution.get(i);
	    int a = e.from, b = e.to;
	    // Kant mellan a och b ingår i vår matchningslösning
	    io.println(a + " " + b);
	}
	
    }
    
    BipRed() {
	io = new Kattio(System.in, System.out);
	
	readBipartiteGraph();
	
	writeFlowGraph();
	
	readMaxFlowSolution();
	
	writeBipMatchSolution();

	// debugutskrift
	//System.err.println("Bipred avslutar\n");

	// Kom ihåg att stänga ner Kattio-klassen
	io.close();
    }
    
    public static void main(String args[]) {
	new BipRed();
    }
}

