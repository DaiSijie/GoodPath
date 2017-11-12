package goodpath.openstreetmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import goodpath.utils.CoordinatesUtils;
import goodpath.utils.LngLat;

public class Graph {
    private final Map<Long, OSMNode> nodes;
    private final Map<Long, List<Edge>> edges;

    public Graph() {
        nodes = new HashMap<>();
        edges = new HashMap<>();
    }

    public void addNode(long id, double lng, double lat) {
        OSMNode node = new OSMNode(id, lng, lat);
        nodes.put(id, node);
        edges.put(id, new ArrayList<>());
    }

    public void addEdge(long id1, long id2) {
        OSMNode node1 = nodes.get(id1);
        OSMNode node2 = nodes.get(id2);
        if(node1 == null || node2 == null) {
            System.out.println("Dropping edge");
            return;
        }

        Edge edge = new Edge(node1.getId(), node2.getId(), computeDistance(node1.getCoord(), node2.getCoord()));
        edges.get(node1.getId()).add(edge);
        edges.get(node2.getId()).add(edge);
    }

    private static double computeDistance(LngLat coord1, LngLat coord2) {
        return CoordinatesUtils.distanceOf(coord1, coord2);
    }

    public Set<Long> getNodeKeys() {
        return nodes.keySet();
    }

    public List<Edge> getEdges(long node) {
        if(edges.containsKey(node)) {
            return edges.get(node);
        } else {
            return Collections.emptyList();
        }
    }

    public OSMNode getNode(long node) {
        return nodes.get(node);
    }

    private OSMNode nearestNode(LngLat coord) {
        double minDist = Double.POSITIVE_INFINITY;
        OSMNode min = null;
        for(OSMNode node: nodes.values()) {
            double dist = computeDistance(node.getCoord(), coord);
            if(dist < minDist) {
                minDist = dist;
                min = node;
            }
        }
        return min;
    }

    public List<LngLat> shortestPath(LngLat from, LngLat to) {
        OSMNode fromNode = nearestNode(from);
        OSMNode toNode = nearestNode(to);

        List<OSMNode> nodes = shortestPath(fromNode, toNode);

        List<LngLat> coords = new ArrayList<>(nodes.size());
        for(OSMNode node: nodes) {
            coords.add(node.getCoord());
        }

        return coords;
    }

    private List<OSMNode> shortestPath(OSMNode from, OSMNode to) {
        ShortestPath shortestPath = new ShortestPath(this, from, to);
        shortestPath.compute();
        return shortestPath.getPath();
    }

    @Override
    public String toString() {
        return edges.size() + " " + nodes.size();
    }
}
