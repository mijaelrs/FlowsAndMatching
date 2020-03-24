#include <iostream>
#include <vector>
#include <array>
#include <queue>
#include <climits>
#include <algorithm>
#include <sstream>

using namespace std;

#define MAX_NODES 4001

struct edge {
    edge(int capacity, int s, int t) {
        this-> capacity = capacity;
        this-> flow = 0;
        this-> s = s;
        this-> t = t;
    }
    int capacity;
    int flow;
    int s;
    int t;
    struct edge *reverse;
};

void clear( std::queue<int> &q ) {
   queue<int> empty;
   swap(q, empty );
}

int calculate_flow(array<vector<edge*>, MAX_NODES> graph, int s, int t, int num_nodes) {
    int flow = 0;
    queue<int> q;
    while(true) {
        q.push(s);
        edge *pred[MAX_NODES] = {nullptr};
        while(!q.empty()) {
            int current = q.front(); q.pop();
            for(edge *e : graph[current]) {
                if(e == nullptr) {
                    continue;
                }
                edge edg = *e;
                if(pred[edg.t] == nullptr && edg.t != s && edg.capacity > edg.flow) {
                    pred[edg.t] = e;
                    q.push(edg.t);
                }
            }
        }
        if(pred[t] == nullptr) {
            clear(q);
            break;
        }
        int df = INT_MAX;
        for(int node = t; node != s; node = pred[node] -> s) {
            df = min(df, (pred[node] -> capacity) - (pred[node] -> flow));
        }
        for(int node = t; node != s; node = pred[node] -> s) {
            pred[node] -> flow += df;
            pred[node] -> reverse -> flow -= df;
        }
        flow += df;
        clear(q);
    }
    return flow;
}

int main(int argc, char const *argv[]) {
    std::ios::sync_with_stdio(false);
    int num_nodes,num_edges,s,t;
    array<vector<edge*>, MAX_NODES> graph;
    fill(graph.begin(), graph.end(), vector<edge*>(5));

    cin >> num_nodes;
    cin >> s >> t;
    cin >> num_edges;

    int u,v,c;
    while(cin >> u >> v >> c) {
        edge *e = new edge(c,u,v);
        edge *rev = new edge(0,v,u);
        e -> reverse = rev;
        rev -> reverse = e;
        graph[u].push_back(e);
        graph[v].push_back(rev);
    }
    int max_flow = calculate_flow(graph,s,t,num_nodes);

    cout << num_nodes << endl;
    cout << s << " " << t << " " << max_flow << endl;

    stringstream output;
    int num_lines = 0;
    for(auto list: graph) {
        for(edge *e: list) {
            if(e != nullptr && e->flow > 0) {
                ++num_lines;
                output << e -> s << " " << e -> t << " " << e -> flow << endl;
            }
            delete e;
        }
    }
    cout << num_lines << endl;
    cout << output.str();
    return EXIT_SUCCESS;
}
