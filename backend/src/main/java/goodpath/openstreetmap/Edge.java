package goodpath.openstreetmap;


public class Edge {
    private final long node1;
    private final long node2;
    private final double distance;
    private double weight;

    public Edge(long n1, long n2, double dist) {
        node1 = n1;
        node2 = n2;
        distance = dist;
        this.weight = 0;
    }

    public double getTotalWeight() { return this.distance + this.weight; }

    public long getOtherNode(long node) {
        if(node == node1) {
            return node2;
        } else {
            return node1;
        }
    }

    public void addWeight(){ this.weight += 2*distance;}
}
