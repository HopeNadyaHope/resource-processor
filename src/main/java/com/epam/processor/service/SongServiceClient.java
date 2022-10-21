package com.epam.processor.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.Map;

@Component
public class SongServiceClient {
    private static final String SONG_SERVICE_APP_NAME = "song-service";
    private static final String SONG_SERVICE_URL_PATTERN = "http://{0}:{1}/songs";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EurekaClient eurekaClient;

    @Retryable
    public void saveSong(String resourceId, JSONObject songMetadataJsonModel) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request =
                new HttpEntity<>(songMetadataJsonModel.toString(), headers);

        String url = getSongServiceUrl();

        restTemplate.postForObject(url, request, Map.class);
    }

    private String getSongServiceUrl() {
        InstanceInfo resourceServiceInfo = eurekaClient
                .getApplication(SONG_SERVICE_APP_NAME)
                .getInstances()
                .get(0);

        String host = resourceServiceInfo.getHostName();
        String port = String.valueOf(resourceServiceInfo.getPort());

        return MessageFormat.format(SONG_SERVICE_URL_PATTERN,
                host, port);
    }
}
