package com.redash.infoservice.apps.metaAdsManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import com.facebook.ads.sdk.*;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private static final String OUTCOME_ENGAGEMENT = "OUTCOME_ENGAGEMENT";
    private static final String LINK_CLICKS = "LINK_CLICKS";
    private static final String OUTCOME_AWARENESS = "OUTCOME_AWARENESS";


    @Value("${infoservice.app.metaAccessToken}")
    private String accessToken;

    public List<ReportDto>  getReport(
            String startDate,
            String endDate,
            String adAccountId,
            String level) throws APIException {
        String accessToken = this.accessToken;
        APIContext context = new APIContext(accessToken).enableDebug(true);
        AdsInsights.EnumLevel enumLevel = getEnumLevel(level);

        APINodeList<AdsInsights> insights = new AdAccount(adAccountId, context).getInsights()
                .setTimeRange("{\"since\":\"" + startDate + "\",\"until\":\"" + endDate + "\"}")
                .setFiltering("[]")
                .setLevel(enumLevel)
                .setBreakdowns(List.of())
                .requestField("reach")
                .requestField("frequency")
                .requestField("impressions")
                .requestField("spend")
                .requestField("attribution_setting")
                .requestField("campaign_name")
                .requestField("clicks")
                .requestField("cost_per_unique_click")
                .requestField("cost_per_inline_link_click")
                .requestField("cost_per_inline_post_engagement")
                .requestField("cpc")
                .requestField("cpm")
                .requestField("cpp")
                .requestField("ctr")
                .requestField("inline_link_click_ctr")
                .requestField("inline_link_clicks")
                .requestField("inline_post_engagement")
                .requestField("adset_id")
                .requestField("adset_name")
                .requestField("cost_per_action_type")
                .requestField("objective")
                .execute();

        return insights.stream().map(insight -> createReportDto(insight, startDate, endDate)).collect(Collectors.toList());
    }

    private AdsInsights.EnumLevel getEnumLevel(String level) throws APIException {
        try {
            return AdsInsights.EnumLevel.valueOf("VALUE_" + level.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new APIException("Invalid level parameter: " + level, e);
        }
    }

    private ReportDto createReportDto(
            AdsInsights insight,
            String startDate,
            String endDate) {
        ReportDto dto = new ReportDto();

        dto.setReach(insight.getFieldReach());
        dto.setFrequency(insight.getFieldFrequency());
        dto.setImpressions(insight.getFieldImpressions());
        dto.setSpend(insight.getFieldSpend());
        dto.setCampaignName(insight.getFieldCampaignName());
        dto.setClicks(insight.getFieldClicks());
        dto.setCostPerUniqueClick(insight.getFieldCostPerUniqueClick());
        dto.setCostPerInlineLinkClick(insight.getFieldCostPerInlineLinkClick());
        dto.setCostPerInlinePostEngagement(insight.getFieldCostPerInlinePostEngagement());
        dto.setCpc(insight.getFieldCpc());
        dto.setCpm(insight.getFieldCpm());
        dto.setCpp(insight.getFieldCpp());
        dto.setCtr(insight.getFieldCtr());
        dto.setInlineLinkClickCtr(insight.getFieldInlineLinkClickCtr());
        dto.setInlineLinkClicks(insight.getFieldInlineLinkClicks());
        dto.setInlinePostEngagement(insight.getFieldInlinePostEngagement());
        dto.setAdsetId(insight.getFieldAdsetId());
        dto.setAdsetName(insight.getFieldAdsetName());
        dto.setObjective(insight.getFieldObjective());
        List<Map<String, String>> costPerActionType = Optional.ofNullable(insight.getFieldCostPerActionType())
                .map(list -> list.stream()
                        .map(action -> Map.of(
                                "actionType", action.getFieldActionType(),
                                "value", action.getFieldValue()
                        ))
                        .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());

        if (OUTCOME_ENGAGEMENT.equals(dto.getObjective())) {
            costPerActionType.stream()
                    .filter(action -> "onsite_conversion.messaging_conversation_started_7d".equals(action.get("actionType")))
                    .findFirst().ifPresent(action -> {
                        dto.setCostPerResult(action.get("value"));
                        try {
                            double spend = Double.parseDouble(dto.getSpend());
                            double costPerResult = Double.parseDouble(action.get("value"));
                            if (costPerResult != 0) {
                                double result = spend / costPerResult;
                                dto.setResult(String.valueOf(result));
                            } else {
                                dto.setResult(String.valueOf(0));
                            }
                        } catch (NumberFormatException e) {
                            dto.setResult(String.valueOf(0));
                        }
                    });
        }

        if (LINK_CLICKS.equals(dto.getObjective())) {
            try {
                double spend = Double.parseDouble(dto.getSpend());
                double costPerResult = Double.parseDouble(dto.getCpc());
                dto.setCostPerResult(String.valueOf(costPerResult));
                if (costPerResult != 0) {
                    double result = spend / costPerResult;
                    dto.setResult(String.valueOf(result));
                } else {
                    dto.setResult(String.valueOf(0));
                }
            } catch (NumberFormatException e) {
                dto.setResult(String.valueOf(0));
            }
        }

        if (OUTCOME_AWARENESS.equals(dto.getObjective())) {
            if (dto.getReach() != null) {
                dto.setResult(dto.getReach());
            } else {
                dto.setResult(String.valueOf(0));
            }
            if (dto.getCpp() != null) {
                dto.setCostPerResult(dto.getCpp());
            } else {
                dto.setCostPerResult(String.valueOf(0));
            }
        }
        dto.setCostPerActionType(costPerActionType);
        dto.setDateStart(startDate);
        dto.setDateStop(endDate);

        return dto;
    }

}