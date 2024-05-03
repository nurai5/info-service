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
public class CampaignReportController {
    private static final Logger logger = LoggerFactory.getLogger(CampaignReportController.class);
    private final CampaignReportService campaignReportService;

    @Autowired
    public CampaignReportController(CampaignReportService campaignReportService) {
        this.campaignReportService = campaignReportService;
    }

    @GetMapping(path = "/campaign-report")
    public ResponseEntity<List<CampaignReportDto>> getCampaignReport(
            @RequestParam("start_date") String startDate,
            @RequestParam("end_date") String endDate,
            @RequestParam("ad_account_id") String adAccountId) throws APIException {
        List<CampaignReportDto> campaignReportDto = campaignReportService.getCampaignReport(
                startDate, endDate, adAccountId
        );

        return ResponseEntity.ok(campaignReportDto);
    }
}
