// Student ID: [YOUR STUDENT ID]
// Name:       [YOUR NAME]

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Implements the sink-elimination algorithm to decide whether a directed
 * graph is acyclic, and—when it is not—outputs an explicit cycle.
 *
 * Algorithm (sink elimination):
 *   1. If the graph is empty → return YES (acyclic).
 *   2. If there is no sink   → return NO  (cycle detected).
 *   3. Remove a sink and go to 1.
 *
 * Correctness sketch:
 *   An acyclic graph always contains a sink (a topological ordering ends at
 *   one).  Removing a sink from an acyclic graph leaves it acyclic, so the
 *   procedure terminates with an empty graph iff the original was acyclic.
 *
 * Complexity: O(V + E) — each vertex and each edge is visited at most once.
 */
public class AcyclicityChecker {

    /**
     * Result object returned by {@link #check}.
     */
    public static class Result {
        public final boolean acyclic;
        /** Vertices eliminated as sinks (in elimination order). Empty if not acyclic. */
        public final List<Integer> eliminationOrder;
        /** A cycle (vertex sequence) when {@code acyclic} is false; empty otherwise. */
        public final List<Integer> cycle;

        Result(boolean acyclic, List<Integer> eliminationOrder, List<Integer> cycle) {
            this.acyclic          = acyclic;
            this.eliminationOrder = eliminationOrder;
            this.cycle            = cycle;
        }
    }

    /**
     * Checks whether {@code graph} is acyclic and prints step-by-step output.
     *
     * The method works on an internal copy so the original graph is unchanged.
     *
     * @param graph the graph to analyse
     * @param verbose if true, each elimination step is printed
     * @return a {@link Result} containing the answer, elimination sequence, and
     *         (if applicable) a cycle
     */
    public static Result check(DirectedGraph graph, boolean verbose) {

        // Work on a copy to preserve the original
        DirectedGraph working = graph.copy();

        List<Integer> eliminationOrder = new ArrayList<>();

        if (verbose) {
            System.out.println("=== Sink Elimination Algorithm ===");
        }

        java.util.Queue<Integer> sinks = new java.util.LinkedList<>();
        for (int v : working.vertices()) {
            if (working.outDegree(v) == 0) {
                sinks.add(v);
            }
        }

        while (!sinks.isEmpty()) {
            Integer sink = sinks.poll();

            eliminationOrder.add(sink);
            if (verbose) {
                System.out.println("Eliminated sink: " + sink
                        + "  | remaining vertices: " + (working.vertices().size() - 1));
            }

            java.util.Set<Integer> preds = working.predecessors(sink);
            working.removeVertex(sink);
            
            for (int p : preds) {
                if (working.containsVertex(p) && working.outDegree(p) == 0) {
                    sinks.add(p);
                }
            }
        }

        if (!working.isEmpty()) {
            if (verbose) {
                System.out.println("No sink found in remaining graph — cycle detected.");
                System.out.println("Remaining vertices: " + working.vertices());
            }
            List<Integer> cycle = findCycle(working);
            return new Result(false, eliminationOrder, cycle);
        }

        if (verbose) {
            System.out.println("Graph is empty — acyclic confirmed.");
        }
        return new Result(true, eliminationOrder, new ArrayList<>());
    }

    // ---------------------------------------------------------------
    // Cycle extraction via DFS
    // ---------------------------------------------------------------

    /**
     * Finds and returns a cycle in {@code graph} as an ordered list of vertices.
     * The returned list starts and ends with the same vertex, e.g. [3, 7, 2, 3].
     *
     * Uses iterative DFS with three-colour marking:
     *   WHITE (0) – not yet visited
     *   GREY  (1) – on the current DFS path (stack)
     *   BLACK (2) – fully processed
     */
    private static List<Integer> findCycle(DirectedGraph graph) {

        final int WHITE = 0, GREY = 1, BLACK = 2;
        Map<Integer, Integer> colour = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();

        Map<Integer, List<Integer>> succMap = new HashMap<>();

        for (int v : graph.vertices()) colour.put(v, WHITE);

        for (int start : graph.vertices()) {
            if (colour.get(start) == WHITE) {
                List<Integer> result = dfs(start, colour, parent, graph, WHITE, GREY, BLACK, succMap);
                if (result != null) return result;
            }
        }
        return new ArrayList<>(); // should not reach here
    }

    private static List<Integer> dfs(int start,
                                     Map<Integer, Integer> colour,
                                     Map<Integer, Integer> parent,
                                     DirectedGraph graph,
                                     int WHITE, int GREY, int BLACK,
                                     Map<Integer, List<Integer>> succMap) {

        // Explicit stack to avoid recursion-depth issues on large graphs.
        // Each stack entry: [vertex, iterator index] — we track how far we've
        // gone through the successors of each vertex.
        java.util.Deque<int[]> stack = new java.util.ArrayDeque<>();

        stack.push(new int[]{start, 0});
        colour.put(start, GREY);

        while (!stack.isEmpty()) {
            int[] top  = stack.peek();
            int   v    = top[0];
            List<Integer> succs = succMap.get(v);
            if (succs == null) {
                succs = new ArrayList<>(graph.successors(v));
                succMap.put(v, succs);
            }

            if (top[1] < succs.size()) {
                int w = succs.get(top[1]++);
                int col = colour.getOrDefault(w, WHITE);

                if (col == GREY) {
                    // Back edge found → reconstruct cycle
                    List<Integer> cycle = new ArrayList<>();
                    cycle.add(w);
                    // Walk parent pointers from v back to w
                    int cur = v;
                    while (cur != w) {
                        cycle.add(0, cur);
                        cur = parent.get(cur);
                    }
                    cycle.add(0, w);
                    return cycle;
                } else if (col == WHITE) {
                    colour.put(w, GREY);
                    parent.put(w, v);
                    stack.push(new int[]{w, 0});
                }
            } else {
                colour.put(v, BLACK);
                stack.pop();
            }
        }
        return null;
    }
}
