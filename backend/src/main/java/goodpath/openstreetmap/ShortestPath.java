package goodpath.openstreetmap;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.goodpaths.common.MyLngLat;

public class ShortestPath {
    private final Graph graph;
    private final OSMNode start;
    private final OSMNode end;

    private final PriorityQueue<Value> values;

    private final Map<Long, Value> valueMap;
    private boolean success = false;

    public ShortestPath(Graph graph, OSMNode start, OSMNode end) {
        this.graph = graph;
        this.start = start;
        this.end = end;

        this.values = new PriorityQueue<>();
        this.valueMap = new HashMap<>();
    }

    public void compute() {
        Value s = new Value(start.getId(), -1, 0);
        values.add(s);
        valueMap.put(start.getId(), s);

        while (!values.isEmpty()) {
            Value current = values.poll();
            long u = current.id;

            if (u == end.getId()) {
                return;
            }

            for(Edge e: graph.getEdges(u)) {
                long vId = e.getOtherNode(u);
                Value v = valueMap.get(vId);
                double alt = getDist(u) + e.getDistance();
                if (v == null) {
                    v = new Value(vId, u, alt);
                    valueMap.put(vId, v);
                    values.add(v);
                } else {
                    double distV = getDist(vId);
                    if (alt < distV) {
                        values.remove(v);
                        v.distance = alt;
                        v.previous = u;
                        values.add(v);
                    }
                }
            }
        }
    }

    public List<OSMNode> getPath() {
        if(!valueMap.containsKey(end.getId())){
            return Collections.emptyList();
        }

        List<Long> reversePath = new ArrayList<>();
        long current = end.getId();
        while (current != -1) {
            reversePath.add(current);
            current = valueMap.get(current).previous;
        }

        List<OSMNode> path = new ArrayList<>(reversePath.size());
        for(int i = reversePath.size() - 1; i >= 0; i--) {
            path.add(graph.getNode(reversePath.get(i)));
        }

        return path;
    }


    private double getDist(long id) {
        if(valueMap.containsKey(id)) {
            return valueMap.get(id).distance;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    public boolean inLausanne(MyLngLat destination){
        if(destination.lng < 6.67264938354492 && destination.lng > 6.542530059814453){
            if(destination.lat < 46.54653595517069 && destination.lat > 46.50968788814508){
                return true;
            }
        }
        return false;
    }


    private static class Value implements Comparable<Value> {
        long id;
        long previous;
        double distance;

        public Value(long id, long previous, double distance) {
            this.id = id;
            this.previous = previous;
            this.distance = distance;
        }

        @Override
        public int compareTo(Value value) {
            int distComp = Double.compare(distance, value.distance);
            if(distComp == 0) {
                return Long.compare(id, value.id);
            } else {
                return distComp;
            }
        }
    }


}
