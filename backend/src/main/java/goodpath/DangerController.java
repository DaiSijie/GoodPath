package goodpath;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.ArrayList;

@RestController
public class DangerController {

    private final ArrayList<Report> reports = new ArrayList<Report>();

    @RequestMapping(path = "/addDanger", method = RequestMethod.POST)
    public Report addDanger(@RequestParam(value="problemType") String reportType,@RequestParam(value="latitude") double latitude, @RequestParam(value="longitude") double longitude){
        Report report = new Report(Report.Type.valueOf(reportType),latitude, longitude);
        this.reports.add(report);
        return report;
    }

    @RequestMapping("getDanger")
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

}
