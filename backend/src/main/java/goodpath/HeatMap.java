package goodpath;


import com.goodpaths.common.Report;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import goodpath.openstreetmap.Graph;
import goodpath.utils.CoordinatesUtils;
import com.goodpaths.common.MyLngLat;

public class HeatMap {
    private final List<MyLngLat> reports;
    private final Report.Type problemType;
    private final Color color;

    public HeatMap(Report.Type problemType) {
        this.reports = new ArrayList<>();
        this.problemType = problemType;

        if(this.problemType == Report.Type.ACCESSIBILITY){
            this.color = new Color(255,200,0,100);
        }
        else if(this.problemType == Report.Type.HARASSMENT){
            this.color = new Color(255,0,0,100);
        }
        else{
            System.out.println("ERROR");
            this.color = new Color(0,0,0,0);
        }
    }

    public void addReport(Report report, Graph graph) {
        graph.addWeight(new MyLngLat(report.getLongitude(),report.getLatitude()));
        reports.add(new MyLngLat(report.getLongitude(), report.getLatitude()));
    }

    public BufferedImage getImage(int x, int y, int zoom, int imageSize) {
        List<MyLngLat> selected = selectReports(x, y, zoom);
        return new HeatMapImage(imageSize).getImage(selected, x, y, zoom, this.color);
    }

    private ArrayList<MyLngLat> selectReports(int x, int y, int zoom) {
        int x_TL = x;
        int x_BR = x + 2;
        int y_TL = y;
        int y_BR = y + 2;

        if (x_TL > 0) {
            --x_TL;
        }

        if (y_TL > 0) {
            --y_TL;
        }

        MyLngLat locationTL = CoordinatesUtils.toWGS84(x_TL, y_TL, zoom);
        MyLngLat locationBR = CoordinatesUtils.toWGS84(x_BR, y_BR, zoom);

        ArrayList<MyLngLat> selected = new ArrayList<>();

        for (MyLngLat lnglat : this.reports) {
            if (isInside(lnglat, locationTL.lng, locationTL.lat, locationBR.lng, locationBR.lat)) {
                selected.add(lnglat);
            }
        }
        return selected;
    }

    private boolean isInside(MyLngLat report, double longitudeTL, double latitudeTL, double longitudeBR, double latitudeBR) {
        double newLongitude = report.lng;

        if (longitudeBR >= 360 && newLongitude < longitudeTL) {
            newLongitude += 360;
        }

        return (latitudeBR <= report.lat && report.lat <= latitudeTL &&
                longitudeTL <= newLongitude && newLongitude <= longitudeBR);
    }

    public void populateRandomly(double lng, double lat, Graph graph) {
        final double delta = 0.01;
        final double prob = 0.9;
        final double deltaFactor = 500;
        MyLngLat coord = null;
        for(int i = 0; i < 10000; i++) {
            if (Math.random() < prob && coord != null) {
                coord = new MyLngLat(randomDouble(coord.lng, delta/deltaFactor), randomDouble(coord.lat, delta/deltaFactor));
            } else {
                coord = new MyLngLat(randomDouble(lng, delta), randomDouble(lat, delta));
            }
            graph.addWeight(coord);
            reports.add(coord);
        }
    }

    public void populateRandomlyDemo(double lng, double lat, Graph graph) {
        final double delta = 0.01;
        final double prob = 0.7;
        final double deltaFactor = 500;
        MyLngLat coord = null;
        for(int i = 0; i < 100; i++) {
            if (Math.random() < prob && coord != null) {
                coord = new MyLngLat(randomDouble(coord.lng, delta/deltaFactor), randomDouble(coord.lat, delta/deltaFactor));
            } else {
                coord = new MyLngLat(randomDouble(lng, delta), randomDouble(lat, delta));
            }
            graph.addWeight(coord);
            reports.add(coord);
        }
    }

    public void populateSmartly(double lng, double lat, Graph graph) {
        final double delta = 0.0003;
        MyLngLat coord = null;
        for(int i = 0; i < 20; i++) {
            coord = new MyLngLat(randomDouble(lng, delta), randomDouble(lat, delta));
            graph.addWeight(coord);
            reports.add(coord);
        }
    }

    private static double randomDouble(double value, double delta) {
        return value - delta + (Math.random() * 2 * delta);
    }
}
