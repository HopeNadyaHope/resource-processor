package com.epam.processor.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceProcessor {
    @Autowired
    private ResourceServiceClient resourceServiceClient;
    @Autowired
    private SongMetadataParser songMetadataParser;
    @Autowired
    private SongServiceClient songServiceClient;

    public void process(String resourceId) {
        byte[] resourceBytes = resourceServiceClient.getResourceBytes(resourceId);
        JSONObject songMetadataJsonModel = songMetadataParser.parseSongMetadata(resourceId, resourceBytes);
        songServiceClient.saveSong(resourceId, songMetadataJsonModel);
    }
}
