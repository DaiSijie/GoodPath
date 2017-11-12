package goodpath;


import com.goodpaths.common.Report;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import goodpath.utils.CoordinatesUtils;
import goodpath.utils.LngLat;

public class HeatMap {
    private final List<LngLat> reports;

    public HeatMap() {
        this.reports = new ArrayList<>();
    }

    public void addReport(Report report) {
        reports.add(new LngLat(report.getLongitude(), report.getLatitude()));
    }

    public BufferedImage getImage(int x, int y, int zoom, int imageSize) {
        List<LngLat> selected = selectReports(x, y, zoom);
        return new HeatMapImage(imageSize).getImage(selected, x, y, zoom);
    }

    private ArrayList<LngLat> selectReports(int x, int y, int zoom) {
        int x_TL = x;
        int x_BR = x + 1;
        int y_TL = y;
        int y_BR = y + 1;

        if (x_TL > 0) {
            --x_TL;
        }

        if (y_TL > 0) {
            --y_TL;
        }

        LngLat locationTL = CoordinatesUtils.toWGS84(x_TL, y_TL, zoom);
        LngLat locationBR = CoordinatesUtils.toWGS84(x_BR, y_BR, zoom);

        ArrayList<LngLat> selected = new ArrayList<>();

        for (LngLat lnglat : this.reports) {
            if (isInside(lnglat, locationTL.lng, locationTL.lat, locationBR.lng, locationBR.lat)) {
                selected.add(lnglat);
            }
        }
        return selected;
    }

    private boolean isInside(LngLat report, double longitudeTL, double latitudeTL, double longitudeBR, double latitudeBR) {
        double newLongitude = report.lng;

        if (longitudeBR >= 360 && newLongitude < longitudeTL) {
            newLongitude += 360;
        }

        return (latitudeBR <= report.lat && report.lat <= latitudeTL &&
                longitudeTL <= newLongitude && newLongitude <= longitudeBR);
    }

    public void populateRandomly(double lng, double lat) {
        final int delta = 1;
        for(int i = 0; i < 100; i++) {
            reports.add(new LngLat(randomDouble(lng, delta), randomDouble(lat, delta)));
        }
    }

    private static double randomDouble(double value, double delta) {
        return value - delta + (Math.random() * 2 * delta);
    }
}
