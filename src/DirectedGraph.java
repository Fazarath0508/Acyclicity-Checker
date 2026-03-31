// Student ID: 20241209
// Name:       MAM.Fazarath Shathaff

import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;

/**
 * Represents a directed graph using an adjacency list.
 *
 * Each vertex is stored as an integer. Two maps are maintained:
 *   - outEdges: maps each vertex to the set of vertices it points TO
 *   - inEdges:  maps each vertex to the set of vertices that point TO it
 *
 * Keeping both maps lets us locate sinks (vertices with empty outEdges)
 * and remove vertices in O(degree) time.
 */
public class DirectedGraph {

    // vertex -> set of successors
    private final Map<Integer, Set<Integer>> outEdges;
    // vertex -> set of predecessors
    private final Map<Integer, Set<Integer>> inEdges;

    // ---------------------------------------------------------------
    // Construction
    // ---------------------------------------------------------------

    /** Create an empty graph. */
    public DirectedGraph() {
        outEdges = new HashMap<>();
        inEdges  = new HashMap<>();
    }

    /**
     * Add a vertex (no-op if it already exists).
     */
    public void addVertex(int v) {
        outEdges.putIfAbsent(v, new HashSet<>());
        inEdges .putIfAbsent(v, new HashSet<>());
    }

    /**
     * Add a directed edge from {@code from} to {@code to}.
     * Implicitly creates both vertices if they do not yet exist.
     */
    public void addEdge(int from, int to) {
        addVertex(from);
        addVertex(to);
        outEdges.get(from).add(to);
        inEdges .get(to)  .add(from);
    }

    // ---------------------------------------------------------------
    // Queries
    // ---------------------------------------------------------------

    /** True when the graph has no vertices left. */
    public boolean isEmpty() {
        return outEdges.isEmpty();
    }

    /** Returns true if the vertex exists in the graph. */
    public boolean containsVertex(int v) {
        return outEdges.containsKey(v);
    }

    /** Returns the current set of vertices (a snapshot copy). */
    public Set<Integer> vertices() {
        return new HashSet<>(outEdges.keySet());
    }

    /** Returns a snapshot of the successors of {@code v}. */
    public Set<Integer> successors(int v) {
        Set<Integer> s = outEdges.get(v);
        return s == null ? new HashSet<>() : new HashSet<>(s);
    }

    /** Returns the out-degree of {@code v}. */
    public int outDegree(int v) {
        Set<Integer> s = outEdges.get(v);
        return s == null ? 0 : s.size();
    }

    /** Returns a snapshot of the predecessors of {@code v}. */
    public Set<Integer> predecessors(int v) {
        Set<Integer> p = inEdges.get(v);
        return p == null ? new HashSet<>() : new HashSet<>(p);
    }

    /**
     * Returns any sink vertex (one with no outgoing edges), or
     * {@code null} if no sink exists.
     */
    public Integer findSink() {
        for (Map.Entry<Integer, Set<Integer>> e : outEdges.entrySet()) {
            if (e.getValue().isEmpty()) {
                return e.getKey();
            }
        }
        return null;
    }

    // ---------------------------------------------------------------
    // Mutation
    // ---------------------------------------------------------------

    /**
     * Removes vertex {@code v} and all edges incident on it.
     */
    public void removeVertex(int v) {
        // Remove all outgoing edges from v
        Set<Integer> successorsCopy = new HashSet<>(outEdges.getOrDefault(v, new HashSet<>()));
        for (int succ : successorsCopy) {
            inEdges.get(succ).remove(v);
        }

        // Remove all incoming edges to v
        Set<Integer> predecessorsCopy = new HashSet<>(inEdges.getOrDefault(v, new HashSet<>()));
        for (int pred : predecessorsCopy) {
            outEdges.get(pred).remove(v);
        }

        outEdges.remove(v);
        inEdges .remove(v);
    }

    // ---------------------------------------------------------------
    // Utility
    // ---------------------------------------------------------------

    /** Returns a human-readable description of the graph. */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vertices: ").append(outEdges.keySet()).append("\n");
        sb.append("Edges:\n");
        for (Map.Entry<Integer, Set<Integer>> e : outEdges.entrySet()) {
            for (int to : e.getValue()) {
                sb.append("  ").append(e.getKey()).append(" -> ").append(to).append("\n");
            }
        }
        return sb.toString();
    }

    /** Returns a deep copy of this graph. */
    public DirectedGraph copy() {
        DirectedGraph g = new DirectedGraph();
        for (int v : outEdges.keySet()) g.addVertex(v);
        for (Map.Entry<Integer, Set<Integer>> e : outEdges.entrySet()) {
            for (int to : e.getValue()) g.addEdge(e.getKey(), to);
        }
        return g;
    }
}
