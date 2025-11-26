package com.smartshop.ai;

public interface OllamaClient {
    String ask(String systemPrompt, String userMessage);
}
