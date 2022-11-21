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

    public void process(String uploadedResourceId) {
        int id = Integer.parseInt(uploadedResourceId);
        byte[] resourceBytes = apiGatewayClient.getResource(id).getBody();
        SongModel songModel = songMetadataParser.parseSongMetadata(id, resourceBytes);
        apiGatewayClient.createSong(songModel);
        apiGatewayClient.permanentResource(id);
    }
}
