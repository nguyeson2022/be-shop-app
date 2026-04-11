package com.project.shopapp.services;

import com.project.shopapp.dtos.ChatRequest;
import com.project.shopapp.dtos.ChatResponse;

public interface ChatBotService {
    ChatResponse analyzeDescription(ChatRequest request);
}
