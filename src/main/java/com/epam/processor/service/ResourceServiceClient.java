package com.epam.processor.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;

@Component
public class ResourceServiceClient {
    private static final String RESOURCE_SERVICE_APP_NAME = "resource-service";
    private static final String RESOURCE_SERVICE_URL_PATTERN = "http://{0}:{1}/resources/{2}";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EurekaClient eurekaClient;

    @Retryable
    public byte[] getResourceBytes(String resourceId) {
        String url = getResourceServiceUrl(resourceId);
        ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(url, byte[].class);
        return responseEntity.getBody();
    }

    private String getResourceServiceUrl(String resourceId) {
        InstanceInfo resourceServiceInfo = eurekaClient
                .getApplication(RESOURCE_SERVICE_APP_NAME)
                .getInstances()
                .get(0);

        String host = resourceServiceInfo.getHostName();
        String port = String.valueOf(resourceServiceInfo.getPort());

        return MessageFormat.format(RESOURCE_SERVICE_URL_PATTERN,
                host, port, resourceId);
    }
}
