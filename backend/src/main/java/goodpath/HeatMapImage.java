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

    public BufferedImage getImage(List<Report> reports, int x, int y, int zoom) {
        Color[] colors = {Color.RED, new Color(255, 0, 0, 0)};
        BufferedImage toReturn = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        float[] fractions = {0.2f, 1.0f};
        float radius = 50;
        Paint paint;


        Graphics2D g = toReturn.createGraphics();


        g.setColor(Color.RED);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Report report : reports) {
            Point<Float> position = onMap(report, x, y, zoom);
            paint = new RadialGradientPaint(position.x, position.y, radius, fractions, colors);
            g.setPaint(paint);
            g.fill(new Ellipse2D.Double(position.x - radius, position.y - radius, radius * 2, radius * 2));
            System.out.println(position.x + " " + position.y);
        }

        g.dispose();
        return toReturn;
    }

    private Point<Float> onMap(Report report, int x, int y, int z) {
        double latEvent = report.getLatitude();
        double lonEvent = report.getLongitude();
        LngLat CoordTL = CoordinatesUtils.toWGS84(x, y, z);
        LngLat CoordBR = CoordinatesUtils.toWGS84(x + 1, y + 1, z);
        double longitudeTL = CoordTL.lng;
        double latitudeTL = CoordTL.lat;
        double longitudeBR = CoordBR.lng;
        double latitudeBR = CoordBR.lat;
        float posx = (float) ((lonEvent - longitudeTL) * 512 / (longitudeBR - longitudeTL));
        float posy = (float) ((latitudeTL - latEvent) * 512 / (latitudeTL - latitudeBR));


        return new Point<>(posx, posy);
    }
}
