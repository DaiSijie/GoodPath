package goodpath;


import com.goodpaths.common.Report;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.List;

import goodpath.utils.CoordinatesUtils;
import goodpath.utils.LngLat;
import goodpath.utils.Point;

public class HeatMapImage {

    private final int size;

    public HeatMapImage(int size) {
        this.size = size;
    }

    private double haversin(double x) {
        return java.lang.Math.pow(java.lang.Math.sin(x / 2), 2);
    }
/*
    private float computeRadius(int x, int y, int zoom){
        LngLat a = CoordinatesUtils.toWGS84(x, y, zoom);
        LngLat b = CoordinatesUtils.toWGS84(x + 1, y, zoom);
        int R = 6378137;
        double distance = 2*R
                * java.lang.Math.asin(java.lang.Math.sqrt(haversin(a.lat - b.lat)
                + java.lang.Math.cos(b.lat)
                * java.lang.Math.cos(a.lat)
                * haversin(a.lng - b.lng)));

        return (float) (500 * 512/distance);

    }
*/
    private float computeRadius(int x, int y, int zoom){
        int size_in_meters = 20;
        LngLat a = CoordinatesUtils.toWGS84(x, y, zoom);
        return (float)(size_in_meters/(156543.03392 * Math.cos(a.lat * Math.PI / 180) / Math.pow(2, zoom)));
    }
    public BufferedImage getImage(List<LngLat> reports, int x, int y, int zoom) {
        Color[] colors = {new Color(255, 0, 0, 100), new Color(255, 0, 0, 0)};
        BufferedImage toReturn = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        float[] fractions = {0.7f, 1.0f};
        float radius = computeRadius(x,y,zoom);
        System.out.println(radius);
        Paint paint;


        Graphics2D g = toReturn.createGraphics();


        g.setColor(Color.RED);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (LngLat report : reports) {
            Point<Float> position = onMap(report, x, y, zoom);
            paint = new RadialGradientPaint(position.x, position.y, radius, fractions, colors);
            g.setPaint(paint);
            g.fill(new Ellipse2D.Double(position.x - radius, position.y - radius, radius * 2, radius * 2));
            System.out.println(position.x + " " + position.y);
        }

        g.dispose();
        return toReturn;
    }

    private Point<Float> onMap(LngLat report, int x, int y, int z) {
        double latEvent = report.lat;
        double lonEvent = report.lng;
        LngLat CoordTL = CoordinatesUtils.toWGS84(x, y, z);
        LngLat CoordBR = CoordinatesUtils.toWGS84(x + 1, y + 1, z);
        double longitudeTL = CoordTL.lng;
        double latitudeTL = CoordTL.lat;
        double longitudeBR = CoordBR.lng;
        double latitudeBR = CoordBR.lat;
        float posx = (float) ((lonEvent - longitudeTL) * 256 / (longitudeBR - longitudeTL));
        float posy = (float) ((latitudeTL - latEvent) * 256 / (latitudeTL - latitudeBR));


        return new Point<>(posx, posy);
    }
}
