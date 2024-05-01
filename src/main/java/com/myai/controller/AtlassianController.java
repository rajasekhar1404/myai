package com.myai.controller;

import com.myai.service.AtlassianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sync")
public class AtlassianController {

    @Autowired
    private AtlassianService atlassianService;

    @GetMapping("")
    public void sync() {
        atlassianService.ingestContent();
    }

}
