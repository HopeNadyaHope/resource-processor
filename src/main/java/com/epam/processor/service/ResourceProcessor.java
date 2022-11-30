package com.epam.processor.service;

import com.epam.processor.client.ApiGatewayClient;
import com.epam.processor.model.SongModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceProcessor {

    private final Logger logger = LoggerFactory.getLogger(ResourceProcessor.class);
    @Autowired
    private SongMetadataParser songMetadataParser;
    @Autowired
    private ApiGatewayClient apiGatewayClient;

    public void process(String uploadedResourceId) {
        int id = Integer.parseInt(uploadedResourceId);

        logger.info("Get resource bytes for resource with id={}", id);
        byte[] resourceBytes = apiGatewayClient.getResource(id).getBody();

        logger.info("Parse song from resource bytes for resource with id={}", id);
        SongModel songModel = songMetadataParser.parseSongMetadata(id, resourceBytes);

        logger.info("Create song for resource with id={}", id);
        apiGatewayClient.createSong(songModel);

        logger.info("Permanent resource with id={}", id);
        apiGatewayClient.permanentResource(id);

        logger.info("Resource with id={} processed", id);
    }
}
