package com.redash.infoservice.apps.metaAdsManager;

import com.facebook.ads.sdk.APIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping(path = "api/meta")
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(path = "/report")
    public ResponseEntity<List<ReportDto>> getReport(
            @RequestParam("start_date") String startDate,
            @RequestParam("end_date") String endDate,
            @RequestParam("ad_account_id") String adAccountId,
            @RequestParam("level") String level) throws APIException {
        List<ReportDto> ReportDto = reportService.getReport(
                startDate, endDate, adAccountId, level
        );

        return ResponseEntity.ok(ReportDto);
    }
}
