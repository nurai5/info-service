package com.redash.infoservice.apps.metaAdsManager;

import com.facebook.ads.sdk.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/token")
public class LLAccessTokenController {

    private final LLAccessTokenService llAccessTokenService;

    @Autowired
    public LLAccessTokenController(LLAccessTokenService llAccessTokenService) {
        this.llAccessTokenService = llAccessTokenService;
    }

    @PostMapping("/long-live-token")
    public ResponseEntity<LLAccessTokenDto> getLongLiveAccessToken(
            @RequestParam("access-token") String accessToken
    ) throws APIException {
        LLAccessTokenDto llAccessTokenDto = llAccessTokenService.obtainLLAccessToken(accessToken);

        return ResponseEntity.ok(llAccessTokenDto);
    }
}
