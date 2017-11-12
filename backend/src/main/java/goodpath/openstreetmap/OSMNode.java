package goodpath.openstreetmap;

import com.goodpaths.common.MyLngLat;

public class OSMNode {
    private final long id;
    private final MyLngLat coord;

    public OSMNode(long id, double lng, double lat) {
        this.id = id;
        this.coord = new MyLngLat(lng, lat);
    }

    public long getId() {
        return id;
    }

    public MyLngLat getCoord() {
        return coord;
    }
}
