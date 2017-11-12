package goodpath.utils;

public class CoordinatesUtils {
    private CoordinatesUtils() {
    }

    public static LngLat toWGS84(int x, int y, int zoom) {

        double s = java.lang.Math.pow(2, zoom);
        double longitude = 360 * x / s - 180;
        double latitude = java.lang.Math.atan(java.lang.Math
                .sinh(java.lang.Math.PI - 2 * java.lang.Math.PI * y / s));
        latitude = latitude * 180 / java.lang.Math.PI;

        return new LngLat(longitude, latitude);
    }

    public static Point<Integer> toCoord(double longitude, double latitude, int zoom) {
        double n = java.lang.Math.pow(2, zoom);
        int xtile = (int) (n * (longitude + 180) / 360);
        int ytile = (int) (n * (1 - (java.lang.Math.log(java.lang.Math.tan(latitude / 180 * java.lang.Math.PI) + 1 / java.lang.Math.cos(latitude / 180 * java.lang.Math.PI)) / java.lang.Math.PI)) / 2);

        return new Point<>(xtile, ytile);
    }
}
