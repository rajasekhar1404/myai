package com.myai.controller;

import com.myai.service.IngestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingest")
public class IngestController {

    @Autowired
    private IngestService ingestService;

    @DeleteMapping("")
    public void deleteAllCollections() {
        ingestService.reset();
    }

}
