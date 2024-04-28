package com.myai.service;

import dev.langchain4j.service.TokenStream;

public interface CustomAiService {

    TokenStream chat(String message);

}
