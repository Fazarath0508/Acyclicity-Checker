// Student ID: [YOUR STUDENT ID]
// Name:       [YOUR NAME]

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads a directed graph from a plain-text file.
 *
 * File format:
 *   [optional first line: vertex count N]
 *   u v        (one directed edge per line, integers separated by whitespace)
 *   ...
 *
 * If the first line contains a single integer it is treated as the vertex
 * count and vertices 0 .. N-1 are pre-registered.  Otherwise the line is
 * interpreted as an edge like all the others.  Both formats are handled
 * automatically.
 */
public class GraphParser {

    /**
     * Parse the file at {@code filePath} and return the resulting graph.
     *
     * @param filePath path to the input file
     * @return the parsed {@link DirectedGraph}
     * @throws IOException if the file cannot be read
     */
    public static DirectedGraph parse(String filePath) throws IOException {
        DirectedGraph graph = new DirectedGraph();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");

                // If the first line is a single integer, treat it as vertex count
                if (firstLine && parts.length == 1) {
                    firstLine = false;
                    try {
                        int n = Integer.parseInt(parts[0]);
                        for (int i = 0; i < n; i++) graph.addVertex(i);
                    } catch (NumberFormatException e) {
                        // Not an integer header — ignore and continue
                    }
                    continue;
                }
                firstLine = false;

                if (parts.length < 2) continue; // malformed line – skip

                try {
                    int from = Integer.parseInt(parts[0]);
                    int to   = Integer.parseInt(parts[1]);
                    graph.addEdge(from, to);
                } catch (NumberFormatException e) {
                    System.err.println("Skipping unreadable line: " + line);
                }
            }
        }
        return graph;
    }
}
