package com.epam.processor.service;

import com.epam.processor.client.ApiGatewayClient;
import com.epam.processor.model.SongModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceProcessor {
    @Autowired
    private SongMetadataParser songMetadataParser;
    @Autowired
    private ApiGatewayClient apiGatewayClient;

    public void process(String resourceId) {
        byte[] resourceBytes = apiGatewayClient.getResource(Integer.parseInt(resourceId)).getBody();
        SongModel songModel = songMetadataParser.parseSongMetadata(resourceId, resourceBytes);
        apiGatewayClient.createSong(songModel);
    }
}
