package goodpath.openstreetmap;


public class Edge {
    private final long node1;
    private final long node2;
    private final double distance;

    public Edge(long n1, long n2, double dist) {
        node1 = n1;
        node2 = n2;
        distance = dist;
    }

    public double getDistance() {
        return distance;
    }

    public long getOtherNode(long node) {
        if(node == node1) {
            return node2;
        } else {
            return node1;
        }
    }
}
