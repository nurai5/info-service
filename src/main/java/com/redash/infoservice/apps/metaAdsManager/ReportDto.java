package com.redash.infoservice.apps.metaAdsManager;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ReportDto {
    private String reach;
    private String frequency;
    private String impressions;
    private String spend;
    private String campaignName;
    private String clicks;
    private String costPerUniqueClick;
    private String costPerInlineLinkClick;
    private String costPerInlinePostEngagement;
    private String cpc;
    private String cpm;
    private String cpp;
    private String ctr;
    private String inlineLinkClickCtr;
    private String inlineLinkClicks;
    private String inlinePostEngagement;
    private String adsetId;
    private String adsetName;
    private String dateStart;
    private String dateStop;
}
