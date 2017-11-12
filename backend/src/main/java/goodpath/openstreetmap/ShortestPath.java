package goodpath.openstreetmap;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import goodpath.utils.Tuple;

public class ShortestPath {
    private final Graph graph;
    private final OSMNode start;
    private final OSMNode end;

    private final Set<Long> allNodes;
    private final Map<Long, Tuple<Long, Double>> values;

    private boolean success = false;

    public ShortestPath(Graph graph, OSMNode start, OSMNode end) {
        this.graph = graph;
        this.start = start;
        this.end = end;

        this.allNodes = new HashSet<>(graph.getNodeKeys());
        this.values = new HashMap<>();
    }

    public void compute() {
        values.put(start.getId(), new Tuple<>(-1L, 0.0));

        while(!allNodes.isEmpty()) {
            if(allNodes.size() % 1000 == 0)
                System.out.println(allNodes.size());

            long u = findMin();
            if(u == end.getId()) {
                success = true;
                return;
            } else if (u == -1) {
                return;
            }

            double distU = getDist(u);
            allNodes.remove(u);

            for(Edge edge: graph.getEdges(u)) {
                double alt = distU + edge.getDistance();
                long n = edge.getOtherNode(u);
                double nDist = getDist(n);
                if(alt < nDist) {
                    values.put(n, new Tuple<>(u, alt));
                }
            }
        }
    }

    public List<OSMNode> getPath() {
        if(!success) {
            return Collections.emptyList();
        }

        long current = end.getId();
        List<OSMNode> reversePath = new ArrayList<>();

        while(current != -1) {
            reversePath.add(graph.getNode(current));
            current = values.get(current).x;
        }
        List<OSMNode> path = new ArrayList<>(reversePath.size());
        for(int i = reversePath.size() - 1; i >= 0; i--) {
            path.add(reversePath.get(i));
        }

        return path;
    }

    private long findMin() {
        double min = Double.POSITIVE_INFINITY;
        long minNode = -1;
        for(long node: allNodes) {
            double dist = getDist(node);
            if(dist < min) {
                min = dist;
                minNode = node;
            }
        }

        return minNode;
    }

    private double getDist(long node) {
        if(!values.containsKey(node)) {
            return Double.POSITIVE_INFINITY;
        } else {
            return values.get(node).y;
        }
    }



}
