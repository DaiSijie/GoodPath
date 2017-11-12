package goodpath;

import com.goodpaths.common.Report;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import goodpath.utils.LngLat;

import goodpath.openstreetmap.Graph;
import goodpath.openstreetmap.MyXmlHandler;
import goodpath.utils.LngLat;

@RestController
public class DangerController {

    private static final Report.Type DEFAULT_TYPE = Report.Type.ACCESSIBILITY;

    private final Map<Report.Type, HeatMap> heatMaps = new HashMap<>();
    private Graph graph;

    @RequestMapping(path = "/addDanger", method = RequestMethod.POST)
    @ResponseBody
    public Report addDanger(@RequestBody Report report) {
        getHeatMap(report.getProblemtype()).addReport(report);
        return report;
    }

    @RequestMapping(value = "/tileRequest", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody()
    public byte[] tileRequest(
            @RequestParam(value = "x") int x,
            @RequestParam(value = "y") int y,
            @RequestParam(value = "zoom") int zoom,
            @RequestParam(value = "problemtype") Report.Type problemType) throws IOException {

        BufferedImage bufferedImage = getTile(problemType, x, y, zoom);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        return baos.toByteArray();
    }
    @RequestMapping(value = "/pathRequest")
    @ResponseBody()
    public ArrayList<LngLat> pathRequest(
            @RequestParam(value = "longDepart") double longDepart,
            @RequestParam(value = "latDepart") double latDepart,
            @RequestParam(value = "longEnd") double longEnd,
            @RequestParam(value = "latEnd") double latEnd){
        LngLat from = new LngLat(longDepart, latDepart);
        LngLat to = new LngLat(longEnd, latEnd);
        List<LngLat> path;
        path = graph.shortestPath(from, to);

        return (ArrayList<LngLat>) path;
    }

    @RequestMapping(value = "/populateAccessibility")
    public void populateAccessibility() {
        HeatMap heatmap = getHeatMap(Report.Type.ACCESSIBILITY);
        heatmap.populateRandomly(7.44744, 46.948090);
    }

    @RequestMapping(value = "/populateHarassment")
    public void populateHarassment() {
        HeatMap heatmap = getHeatMap(Report.Type.HARASSMENT);
        heatmap.populateRandomly(7.44744, 46.948090);
    }
    @RequestMapping(path = "/smartPopulate", method = RequestMethod.POST)
    @ResponseBody
    public void populate(@RequestBody Report report) {
        HeatMap heatmap = getHeatMap(report.getProblemtype());
        heatmap.populateSmartly(report.getLongitude(), report.getLatitude());
    }

    @RequestMapping(value = "/test")
    public List<LngLat> test() {
        return graph.shortestPath(new LngLat(6.6623399, 46.5597872), new LngLat(6.6596945, 46.5560183));
    }

    @PostConstruct
    public void init() {
        graph = MyXmlHandler.parse();
    }


    private BufferedImage getTile(Report.Type problemType, int x, int y, int zoom) {
        HeatMap heatMap = getHeatMap(problemType);
        return heatMap.getImage(x, y, zoom, 256);
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter crlf = new CommonsRequestLoggingFilter();
        crlf.setIncludeClientInfo(true);
        crlf.setIncludeQueryString(true);
        crlf.setIncludePayload(true);
        return crlf;
    }


    private HeatMap getHeatMap(Report.Type problemType) {
        if (!heatMaps.containsKey(problemType)) {
            heatMaps.put(problemType, new HeatMap(problemType));
        }

        return heatMaps.get(problemType);
    }

}
