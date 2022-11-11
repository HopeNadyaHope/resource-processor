package com.epam.microservices.service;

import com.epam.processor.client.ApiGatewayClient;
import com.epam.processor.model.SongModel;
import com.epam.processor.service.ResourceProcessor;
import com.epam.processor.service.SongMetadataParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
class ResourceProcessorTest {
    @MockBean
    private SongMetadataParser songMetadataParser;
    @MockBean
    private ApiGatewayClient apiGatewayClient;

    @InjectMocks
    private ResourceProcessor processor;

    @Test
    void processTest() {
        int resourceId = 3;
        byte[] resourceBytes = new byte[3];
        SongModel songModel = new SongModel();

        when(apiGatewayClient.getResource(3)).thenReturn(new ResponseEntity<>(resourceBytes, HttpStatus.OK));
        when(songMetadataParser.parseSongMetadata(resourceId, resourceBytes)).thenReturn(songModel);
        when(apiGatewayClient.createSong(songModel)).thenReturn(Map.of());

        processor.process(String.valueOf(resourceId));
        verify(apiGatewayClient).getResource(resourceId);
        verify(apiGatewayClient).createSong(songModel);
        songMetadataParser.parseSongMetadata(resourceId, resourceBytes);
    }
}
