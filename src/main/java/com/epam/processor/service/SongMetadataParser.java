package com.epam.processor.service;

import com.epam.processor.model.SongModel;
import com.epam.processor.service.exception.UnableToParseMetadataException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.epam.processor.constant.SongMetadataConstant.*;

@Component
public class SongMetadataParser {
    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{4})");
    private final Logger logger = LoggerFactory.getLogger(SongMetadataParser.class);

    public SongModel parseSongMetadata(int resourceId, byte[] resourceBytes) {
        Metadata songMetadata = getSongMetadata(resourceBytes);
        return createSongModel(resourceId, songMetadata);
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

    private SongModel createSongModel(int resourceId, Metadata metadata) {
        SongModel songModel = new SongModel();
        songModel.setName(metadata.get(TITLE));
        songModel.setArtist(metadata.get(ARTIST));
        songModel.setAlbum(metadata.get(ALBUM));
        songModel.setLength(metadata.get(DURATION));
        songModel.setResourceId(resourceId);
        songModel.setYear(getYear(metadata.get(RELEASE_DATE)));
        return songModel;
    }

    private int getYear(String releaseDate) {
        Matcher matcher = YEAR_PATTERN.matcher(releaseDate);
        if (!matcher.find()) {
            throw new UnableToParseMetadataException();
        }
        return Integer.parseInt(matcher.group());
    }
}

