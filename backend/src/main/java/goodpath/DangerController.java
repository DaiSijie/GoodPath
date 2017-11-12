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
import java.util.HashMap;
import java.util.Map;

@RestController
public class DangerController {

    private static final Report.Type DEFAULT_TYPE = Report.Type.ACCESSIBILITY;

    private final Map<Report.Type, HeatMap> heatMaps = new HashMap<>();

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
            @RequestParam(value = "zoom") int zoom) throws IOException {

        BufferedImage bufferedImage = getTile(DEFAULT_TYPE, x, y, zoom);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        return baos.toByteArray();
    }

    @RequestMapping(value = "/populate")
    public void populate() {
        HeatMap heatmap = getHeatMap(DEFAULT_TYPE);
        heatmap.populateRandomly(7.44744, 46.948090);
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
            heatMaps.put(problemType, new HeatMap());
        }

        return heatMaps.get(problemType);
    }

}
