package goodpath;

import com.goodpaths.common.Report;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@RestController
public class DangerController {

    private final ArrayList<Report> reports = new ArrayList<Report>();

    @RequestMapping(path = "/addDanger", method = RequestMethod.POST)
    @ResponseBody
    public Report addDanger(@RequestBody Report report){
        this.reports.add(report);


        return report;
    }

    @RequestMapping("/getDanger")
    public int getDanger(){
        return this.reports.size();
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter crlf = new CommonsRequestLoggingFilter();
        crlf.setIncludeClientInfo(true);
        crlf.setIncludeQueryString(true);
        crlf.setIncludePayload(true);
        return crlf;
    }
    
    @RequestMapping("/getColor")
    public int getColor(@RequestParam(value="problemType") Report.Type problemType,
                        @RequestParam(value="latitudeTL") double latitudeTL, @RequestParam(value="longitudeTL") double longitudeTL,
                        @RequestParam(value="latitudeBR") double latitudeBR, @RequestParam(value="longitudeBR") double longitudeBR){
        ArrayList<Double> newLongitudes = longitudeCorrector(longitudeTL,longitudeBR);

        //int nb = getReportsNumber(problemType, latitudeTL, newLongitudes.get(0), latitudeBR, newLongitudes.get(1));

        return 1;
    }

    public static ArrayList<Double>  longitudeCorrector(double longitudeTL, double longitudeBR) {
        ArrayList<Double> longitudes = new ArrayList<Double>();
        if(longitudeTL < 0) {
            longitudeTL = 360 + longitudeTL;
        }
        if(longitudeBR < 0) {
            longitudeBR = 360 + longitudeBR;
        }
        if(longitudeBR < longitudeTL) {
            longitudeBR += 360;
        }
        longitudes.add(longitudeTL);
        longitudes.add(longitudeBR);
        return longitudes;
    }

    /*
    public int getReportsNumber(Report.Type problemType, double latitudeTL, double longitudeTL, double latitudeBR, double longitudeBR){
        int number = 0;

        for(Report report: reports){
            double newLongitude = report.getLongitude();

            if(longitudeBR>=360 && newLongitude < longitudeTL){
                newLongitude+=360;
            }
            System.out.println(latitudeBR + "<=" + report.getLatitude() + "<=" + latitudeTL);
            System.out.println(longitudeTL + "<=" + newLongitude + "<=" + longitudeBR);
            if(latitudeBR <= report.getLatitude() && report.getLatitude() <= latitudeTL &&
                    longitudeTL <= newLongitude && newLongitude <= longitudeBR &&
                    report.getType() == problemType){
                ++number;
                System.out.println("ok");
            }
        }

        return number;
    }

    */

    @RequestMapping(value = "/tileRequest", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody()
    public byte[] tileRequest(@RequestParam(value="x") int x,@RequestParam(value="y") int y, @RequestParam(value="zoom") int zoom) throws IOException {
        BufferedImage bufferedImage = getTile(x,y,zoom);

        //saveImage(bufferedImage,"tile.png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        return baos.toByteArray();
    }

    private BufferedImage getTile(int x, int y, int zoom) {
        BufferedImage toReturn = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = toReturn.createGraphics();


        g.setColor(Color.RED);

        g.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(Color.RED);



        ArrayList<Report> reportsSelection = selectReports(x, y, zoom);

        for(Report report : this.reports){
            ArrayList<Double> position = onMap(report,x,y,zoom);
            g.draw(new Ellipse2D.Double(position.get(0)-15, position.get(1)-15,30,30));
        }







        return toReturn;
    }

    private void saveImage(BufferedImage bufferedImage, String name) throws IOException {
        File outputfile = new File(name);
        ImageIO.write(bufferedImage, "png", outputfile);
    }

    ArrayList<Report> selectReports(int x, int y, int zoom){
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

        ArrayList<Double> locationTL = toWGS84(x_TL, y_TL,  zoom);
        ArrayList<Double> locationBR = toWGS84(x_BR, y_BR,  zoom);

        ArrayList<Report> selected = new ArrayList<>();

        for(Report report : this.reports){
            if(isInside(report, locationTL.get(0), locationTL.get(1), locationBR.get(0), locationBR.get(1))){
                selected.add(report);
            }
        }
        return selected;
    }

    boolean isInside(Report report,  double longitudeTL, double latitudeTL, double longitudeBR ,double latitudeBR) {
        double newLongitude = report.getLongitude();

        if (longitudeBR >= 360 && newLongitude < longitudeTL) {
            newLongitude += 360;
        }

        return ( latitudeBR <= report.getLatitude() && report.getLatitude() <= latitudeTL &&
                longitudeTL <= newLongitude && newLongitude <= longitudeBR );
    }

    ArrayList<Double> toWGS84(int x, int y, int zoom){
        ArrayList<Double> location = new ArrayList<Double>();

        double s = java.lang.Math.pow(2, zoom );
        double longitude = 360* x / s - 180;
        double latitude = java.lang.Math.atan(java.lang.Math
                .sinh(java.lang.Math.PI - 2 * java.lang.Math.PI * y / s));
        latitude = latitude * 180/java.lang.Math.PI;

        location.add(longitude);
        location.add(latitude);

        return location;
    }

    ArrayList<Double> onMap(Report report, int x, int y, int z){
        ArrayList<Double> Point = new ArrayList<Double>();
        double latEvent = report.getLatitude();
        double lonEvent = report.getLongitude();
        ArrayList<Double> CoordTL = toWGS84(x, y, z);
        ArrayList<Double> CoordBR = toWGS84(x+1, y+1, z);
        double longitudeTL =CoordTL.get(0);
        double latitudeTL =	CoordTL.get(1);
        double longitudeBR = CoordBR.get(0);
        double latitudeBR = CoordBR.get(1);
        double posx = ((lonEvent-longitudeTL)*512/(longitudeBR-longitudeTL));
        double posy = ((latitudeTL- latEvent)*512/(latitudeTL - latitudeBR));

        Point.add(posx);
        Point.add(posy);

        return Point;
    }

}
