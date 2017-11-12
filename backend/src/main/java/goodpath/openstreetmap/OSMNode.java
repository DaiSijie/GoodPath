package goodpath.openstreetmap;

import goodpath.utils.LngLat;

public class OSMNode {
    private final long id;
    private final LngLat coord;

    public OSMNode(long id, double lng, double lat) {
        this.id = id;
        this.coord = new LngLat(lng, lat);
    }

    public long getId() {
        return id;
    }

    public LngLat getCoord() {
        return coord;
    }
}
