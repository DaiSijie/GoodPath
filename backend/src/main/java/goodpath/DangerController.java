package goodpath;

import com.goodpaths.common.MyLngLat;
import com.goodpaths.common.Report;
import com.goodpaths.common.ShortestPathQuery;
import com.goodpaths.common.ShortestPathResult;

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

import goodpath.openstreetmap.Graph;
import goodpath.openstreetmap.MyXmlHandler;

@RestController
public class DangerController {

    private final Map<Report.Type, HeatMap> heatMaps = new HashMap<>();
    private Map<Report.Type, Graph> graphs;

    @RequestMapping(path = "/addDanger", method = RequestMethod.POST)
    @ResponseBody
    public Report addDanger(@RequestBody Report report) {
        getHeatMap(report.getProblemtype()).addReport(report, graphs.get(report.getProblemtype()));
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

    @RequestMapping(value = "/pathRequest", method = RequestMethod.POST)
    @ResponseBody()
    public ShortestPathResult pathRequest(@RequestBody ShortestPathQuery query) {
        return new ShortestPathResult(graphs.get(query.getProblemtype()).shortestPath(query.getStart(), query.getEnd()));
    }


    @RequestMapping(value = "/populateAccessibility")
    public void populateAccessibility() {
        HeatMap heatmap = getHeatMap(Report.Type.ACCESSIBILITY);
        heatmap.populateRandomly(6.63050615, 46.52187886, graphs.get(Report.Type.ACCESSIBILITY));
    }

    @RequestMapping(value = "/populateHarassment")
    public void populateHarassment() {
        HeatMap heatmap = getHeatMap(Report.Type.HARASSMENT);
        heatmap.populateRandomly(6.63050615, 46.52187886, graphs.get(Report.Type.HARASSMENT));
    }

    @RequestMapping(path = "/smartPopulate", method = RequestMethod.POST)
    @ResponseBody
    public void populate(@RequestBody Report report) {
        HeatMap heatmap = getHeatMap(report.getProblemtype());
        heatmap.populateSmartly(report.getLongitude(), report.getLatitude(), graphs.get(report.getProblemtype()));
    }


    @RequestMapping(value = "/populate")
    public void populateHarassmentDemo() {
        HeatMap heatmap = getHeatMap(Report.Type.HARASSMENT);
        heatmap.populateRandomlyDemo(6.63050615, 46.52187886, graphs.get(Report.Type.HARASSMENT));
        heatmap.populateSmartly(6.610663533210754, 46.52260231864355, graphs.get(Report.Type.HARASSMENT));
        heatmap.populateSmartly(6.624442040920258, 46.52124841127639, graphs.get(Report.Type.HARASSMENT));
    }

    @RequestMapping(value = "/test")
    public List<MyLngLat> test() {
        return graphs.get(Report.Type.HARASSMENT).shortestPath(new MyLngLat(6.57185196876, 46.521619004), new MyLngLat(6.56410574, 46.52414369));
    }

    @PostConstruct
    public void init() {
        graphs = new HashMap<>();
        graphs.put(Report.Type.ACCESSIBILITY, MyXmlHandler.parse());
        graphs.put(Report.Type.HARASSMENT, MyXmlHandler.parse());
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


