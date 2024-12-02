package com.bopcon.backend.controller;

import com.bopcon.backend.api.SetlistApiClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/setlists")
public class TestController {
    private final SetlistApiClient setlistApiClient;

    @Autowired
    public TestController(SetlistApiClient setlistApiClient) {
        this.setlistApiClient = setlistApiClient;
    }

    @GetMapping("/{mbid}")
    public JsonNode getSetlists(@PathVariable String mbid) {
        return setlistApiClient.fetchSetlists(mbid);
    }
}