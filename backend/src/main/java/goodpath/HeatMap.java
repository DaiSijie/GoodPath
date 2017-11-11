package goodpath;


import com.goodpaths.common.Report;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import goodpath.utils.CoordinatesUtils;
import goodpath.utils.LngLat;

public class HeatMap {
    private final List<Report> reports;

    public HeatMap() {
        this.reports = new ArrayList<>();
    }

    public void addReport(Report report) {
        reports.add(report);
    }

    public BufferedImage getImage(int x, int y, int zoom, int imageSize) {
        List<Report> selected = selectReports(x, y, zoom);
        return new HeatMapImage(imageSize).getImage(selected, x, y, zoom);
    }

    private ArrayList<Report> selectReports(int x, int y, int zoom){
        int x_TL = x;
        int x_BR = x+1;
        int y_TL = y;
        int y_BR = y+1;

        if(x_TL > 0){
            --x_TL;
        }

        if(y_TL > 0){
            --y_TL;
        }

        LngLat locationTL = CoordinatesUtils.toWGS84(x_TL, y_TL,  zoom);
        LngLat locationBR = CoordinatesUtils.toWGS84(x_BR, y_BR,  zoom);

        ArrayList<Report> selected = new ArrayList<>();

        for(Report report : this.reports){
            if(isInside(report, locationTL.lng, locationTL.lat, locationBR.lng, locationBR.lat)){
                selected.add(report);
            }
        }
        return selected;
    }
    private boolean isInside(Report report,  double longitudeTL, double latitudeTL, double longitudeBR ,double latitudeBR) {
        double newLongitude = report.getLongitude();

        if (longitudeBR >= 360 && newLongitude < longitudeTL) {
            newLongitude += 360;
        }

        return ( latitudeBR <= report.getLatitude() && report.getLatitude() <= latitudeTL &&
                longitudeTL <= newLongitude && newLongitude <= longitudeBR );
    }
}
