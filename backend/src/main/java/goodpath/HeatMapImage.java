package goodpath;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.List;

import goodpath.utils.CoordinatesUtils;
import com.goodpaths.common.MyLngLat;
import goodpath.utils.Point;

public class HeatMapImage {

    private final int size;

    public HeatMapImage(int size) {
        this.size = size;
    }

    private double haversin(double x) {
        return java.lang.Math.pow(java.lang.Math.sin(x / 2), 2);
    }

    private float computeRadius(int x, int y, int zoom){
        int size_in_meters = 20;
        MyLngLat a = CoordinatesUtils.toWGS84(x, y, zoom);
        return (float)(size_in_meters/(156543.03392 * Math.cos(a.lat * Math.PI / 180) / Math.pow(2, zoom)));
    }

    public BufferedImage getImage(List<MyLngLat> reports, int x, int y, int zoom, Color color) {
        BufferedImage toReturn = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        float radius = computeRadius(x,y,zoom);


        Graphics2D g = toReturn.createGraphics();


        g.setColor(color);

        RenderingHints rh = new RenderingHints(null);
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        rh.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.addRenderingHints(rh);

        for (MyLngLat report : reports) {
            Point<Float> position = onMap(report, x, y, zoom);
            g.fill(new Ellipse2D.Double(position.x - radius, position.y - radius, radius * 2, radius * 2));
        }
        g.dispose();
        return toReturn;
    }

    private Point<Float> onMap(MyLngLat report, int x, int y, int z) {
        double latEvent = report.lat;
        double lonEvent = report.lng;
        MyLngLat CoordTL = CoordinatesUtils.toWGS84(x, y, z);
        MyLngLat CoordBR = CoordinatesUtils.toWGS84(x + 1, y + 1, z);
        double longitudeTL = CoordTL.lng;
        double latitudeTL = CoordTL.lat;
        double longitudeBR = CoordBR.lng;
        double latitudeBR = CoordBR.lat;
        float posx = (float) ((lonEvent - longitudeTL) * 256 / (longitudeBR - longitudeTL));
        float posy = (float) ((latitudeTL - latEvent) * 256 / (latitudeTL - latitudeBR));

        return new Point<>(posx, posy);
    }

}
