package com.myai.controller;

import com.myai.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/query")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("")
    public Flux<String> getAnswer(@RequestBody String question) {
        return chatService.getResponse(question);
    }

    @GetMapping("")
    public String getDocs() {
        return chatService.getDocuments();
    }

}
