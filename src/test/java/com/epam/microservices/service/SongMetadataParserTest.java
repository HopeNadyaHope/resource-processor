package com.epam.microservices.service;

import com.epam.processor.model.SongModel;
import com.epam.processor.service.SongMetadataParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
class SongMetadataParserTest {

    SongMetadataParser songMetadataParser = new SongMetadataParser();

    @Test
    void parseSongMetadataTest() throws IOException {
        int resourceId = 3;
        InputStream inputStream = this.getClass().getResourceAsStream("/resourceBytes.txt");
        byte[] resourceBytes = inputStream.readAllBytes();

        SongModel songModel = songMetadataParser.parseSongMetadata(resourceId, resourceBytes);

        assertEquals("Perfect", songModel.getName());
        assertEquals("Ed Sheeran", songModel.getArtist());
        assertEquals("Divide (Deluxe Edition)", songModel.getAlbum());
        assertEquals("263436.65625", songModel.getLength());
        assertEquals(resourceId, songModel.getResourceId());
        assertEquals(2017, songModel.getYear());
    }
}

