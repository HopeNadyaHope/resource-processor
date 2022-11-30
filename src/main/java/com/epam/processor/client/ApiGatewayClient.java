package com.epam.processor.client;

import com.epam.processor.model.SongModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Map;

@FeignClient(name = "api-gateway-service")
public interface ApiGatewayClient {

    @GetMapping(value = "/resources/{id}")
    ResponseEntity<byte[]> getResource(@PathVariable(name = "id") Integer id);

    @PutMapping(value = "resources/permanent/{id}")
    void permanentResource(@PathVariable(name = "id") Integer id);

    @PostMapping(value = "/songs", consumes = "application/json")
    Map<String, Integer> createSong(SongModel songModel);
}
