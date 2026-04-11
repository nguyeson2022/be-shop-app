package com.project.shopapp.controllers;

import com.project.shopapp.dtos.ChatRequest;
import com.project.shopapp.dtos.ResponseDTO;
import com.project.shopapp.services.ChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/chatbot")
@RequiredArgsConstructor
@CrossOrigin
public class GeminiController {
    private final ChatBotService geminiService;

    @PostMapping("/analyze")
    public ResponseEntity<ResponseDTO> analyze(@RequestBody ChatRequest request) {
        ResponseDTO responseDTO = ResponseDTO.success(geminiService.analyzeDescription(request));
        return ResponseEntity.ok(responseDTO);
    }
}
