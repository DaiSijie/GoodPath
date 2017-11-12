package goodpath.utils;

public class CoordinatesUtils {
    private CoordinatesUtils() {
    }

    public static LngLat toWGS84(int x, int y, int zoom) {
/*
        double s = java.lang.Math.pow(2, zoom);
        double longitude = 360 * x / s - 180;
        double latitude = java.lang.Math.atan(java.lang.Math
                .sinh(java.lang.Math.PI - 2 * java.lang.Math.PI * y / s));
        latitude = latitude * 180 / java.lang.Math.PI;
*/
        double n = Math.PI - ((2.0 * Math.PI * y) / Math.pow(2.0, zoom));

        float X = (float)((x / Math.pow(2.0, zoom) * 360.0) - 180.0);
        float Y = (float)(180.0 / Math.PI * Math.atan(Math.sinh(n)));

        return new LngLat(X, Y);
    }

    public static Point<Integer> toCoord(double longitude, double latitude, int zoom) {
        double n = java.lang.Math.pow(2, zoom);
        int xtile = (int) (n * (longitude + 180) / 360);
        int ytile = (int) (n * (1 - (java.lang.Math.log(java.lang.Math.tan(latitude / 180 * java.lang.Math.PI) + 1 / java.lang.Math.cos(latitude / 180 * java.lang.Math.PI)) / java.lang.Math.PI)) / 2);

        return new Point<>(xtile, ytile);
    }
    public static double distanceOf(LngLat a, LngLat b) {
        int R = 6378137;
        return 2
                * R
                * java.lang.Math.asin(java.lang.Math.sqrt(haversin(Math.toRadians(a.lat - b.lat))
                + java.lang.Math.cos(Math.toRadians(b.lat))
                * java.lang.Math.cos(Math.toRadians(a.lat))
                * haversin(Math.toRadians(a.lng - b.lng))));
    }

    private static double haversin(double x) {
        return java.lang.Math.pow(java.lang.Math.sin(x / 2), 2);
    }
}
