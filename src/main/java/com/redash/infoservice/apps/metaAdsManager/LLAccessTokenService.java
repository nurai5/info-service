package com.redash.infoservice.apps.metaAdsManager;

import com.facebook.ads.sdk.APIException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

@Service
public class LLAccessTokenService {

    @Value("${infoservice.app.apiVersion}")
    private String apiVersion;

    @Value("${infoservice.app.metaAppSecret}")
    private String appSecret;

    @Value("${infoservice.app.metaAppId}")
    private String appId;

    public LLAccessTokenDto obtainLLAccessToken(String accessToken) throws APIException {
        String urlString = String.format(
                "https://graph.facebook.com/%s/oauth/access_token?grant_type=fb_exchange_token&client_id=%s&client_secret=%s&fb_exchange_token=%s",
                apiVersion, appId, appSecret, accessToken);

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // HTTP_OK = 200
                String response = new BufferedReader(new InputStreamReader(connection.getInputStream()))
                        .lines().collect(Collectors.joining("\n"));

                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return objectMapper.readValue(response, LLAccessTokenDto.class);
                } catch (IOException e) {
                    throw new APIException("Failed to parse JSON response", e);
                }

            } else {
                String errorResponse = new BufferedReader(new InputStreamReader(connection.getErrorStream()))
                        .lines().collect(Collectors.joining("\n"));
                throw new APIException("Failed to fetch data: " + responseCode + " - " + errorResponse);
            }
        } catch (IOException e) {
            throw new APIException("Network error while fetching data", e);
        }
    }
}
