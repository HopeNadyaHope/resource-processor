package com.epam.processor.service;

import com.epam.processor.constant.SongConstant;
import com.epam.processor.service.exception.UnableToParseMetadataException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;

import static com.epam.processor.constant.SongMetadataConstant.*;

@Component
@RabbitListener(queues = "uploaded_resourcesIds_queue")
public class RabbitMQReceiver {
    private static final String RECEIVED_RESOURCE_ID = "Received resource id {0}";
    private static final String RESOURCE_SERVICE_URL = "http://localhost:8080/resources/";
    private static final String SONG_SERVICE_URL = "http://localhost:8010/songs/";
    private final Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @RabbitHandler
    public void process(String resourceId) {
        String resourceIdReceived = MessageFormat.format(RECEIVED_RESOURCE_ID, resourceId);
        logger.info(resourceIdReceived);

        byte[] resourceBytes = getResourceBytes(resourceId);
        Metadata metadata = getSongMetadata(resourceBytes);
        sendSongToSongService(resourceId, metadata);
    }

    @Retryable
    private byte[] getResourceBytes(String resourceId) {
        String url = RESOURCE_SERVICE_URL + resourceId;
        ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(url, byte[].class);
        return responseEntity.getBody();
    }

    private Metadata getSongMetadata(byte[] resourceBytes) {
        InputStream input = new ByteArrayInputStream(resourceBytes);
        ContentHandler handler = new DefaultHandler();
        Metadata metadata = new Metadata();
        Parser parser = new Mp3Parser();
        ParseContext parseCtx = new ParseContext();
        try {
            parser.parse(input, handler, metadata, parseCtx);
            input.close();
        } catch (IOException | SAXException | TikaException e) {
            throw new UnableToParseMetadataException();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return metadata;
    }

    @Retryable
    private void sendSongToSongService(String resourceId, Metadata metadata) {
        JSONObject songJsonModel = createJsonRequest(resourceId, metadata);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request =
                new HttpEntity<>(songJsonModel.toString(), headers);

        restTemplate.postForObject(SONG_SERVICE_URL, request, Map.class);
    }

    private JSONObject createJsonRequest(String resourceId, Metadata metadata) {
        JSONObject songJsonModel = new JSONObject();
        songJsonModel.put(SongConstant.NAME, metadata.get(TITLE));
        songJsonModel.put(SongConstant.ARTIST, metadata.get(ARTIST));
        songJsonModel.put(SongConstant.ALBUM, metadata.get(ALBUM));
        songJsonModel.put(SongConstant.LENGTH, metadata.get(DURATION));
        songJsonModel.put(SongConstant.RESOURCE_ID, resourceId);
        songJsonModel.put(SongConstant.YEAR, metadata.get(YEAR));
        return songJsonModel;
    }
}