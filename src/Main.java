// Student ID: 20241209
// Name:       MAM.Fazarath Shathaff

import java.io.IOException;
import java.util.List;

/**
 * Entry point for the acyclicity-checking tool.
 *
 * Usage:
 *   java Main <inputFile>           – check a single graph (verbose output)
 *   java Main --bench <dir> <runs>  – benchmark all files under dir (quiet)
 *
 * Examples:
 *   java Main graph.txt
 *   java Main --bench benchmarks/acyclic 5
 */
public class Main {

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            printUsage();
            return;
        }

        if (args[0].equals("--bench") && args.length >= 3) {
            runBenchmark(args[1], Integer.parseInt(args[2]));
        } else {
            runSingle(args[0]);
        }
    }

    // ------------------------------------------------------------------
    // Single-file mode
    // ------------------------------------------------------------------

    private static void runSingle(String filePath) throws IOException {
        System.out.println("Reading graph from: " + filePath);
        DirectedGraph graph = GraphParser.parse(filePath);
        System.out.println("Vertices: " + graph.vertices().size());
        System.out.println();

        AcyclicityChecker.Result result = AcyclicityChecker.check(graph, true);
        System.out.println();

        if (result.acyclic) {
            System.out.println("RESULT: YES — the graph is acyclic.");
            System.out.println("Sink elimination order: " + result.eliminationOrder);
        } else {
            System.out.println("RESULT: NO — the graph contains a cycle.");
            System.out.println("Sinks eliminated before cycle detected: " + result.eliminationOrder);
            System.out.println("Cycle found: " + formatCycle(result.cycle));
        }
    }

    // ------------------------------------------------------------------
    // Benchmark mode
    // ------------------------------------------------------------------

    /**
     * Runs every .txt file found directly inside {@code dir},
     * repeating each {@code runs} times and printing timing data.
     */
    private static void runBenchmark(String dir, int runs) throws IOException {
        java.io.File folder = new java.io.File(dir);
        java.io.File[] files = folder.listFiles(
                (d, n) -> n.endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("No .txt files found in: " + dir);
            return;
        }
        java.util.Arrays.sort(files);

        System.out.printf("%-35s %8s %12s%n", "File", "Result", "Avg time (ms)");
        System.out.println("-".repeat(60));

        for (java.io.File f : files) {
            DirectedGraph graph = GraphParser.parse(f.getAbsolutePath());
            String answer = "";
            long total = 0;

            for (int r = 0; r < runs; r++) {
                long t0 = System.nanoTime();
                AcyclicityChecker.Result res = AcyclicityChecker.check(graph, false);
                long t1 = System.nanoTime();
                total  += (t1 - t0);
                answer  = res.acyclic ? "YES" : "NO";
            }
            double avgMs = total / 1_000_000.0 / runs;
            System.out.printf("%-35s %8s %12.3f%n", f.getName(), answer, avgMs);
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static String formatCycle(List<Integer> cycle) {
        if (cycle.isEmpty()) return "(none)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cycle.size(); i++) {
            sb.append(cycle.get(i));
            if (i < cycle.size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java Main <inputFile>");
        System.out.println("  java Main --bench <directory> <runsPerFile>");
    }
}
