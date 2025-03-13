package com.example.myapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.myapp.service.CollectionService;
import com.example.myapp.service.CollectionSetService;
import com.example.myapp.service.GeminiService;
import com.example.myapp.repository.QuestionRepository;
import com.example.myapp.repository.CollectionRepository;
import com.example.myapp.dto.AiQuestionRequest;
import com.example.myapp.dto.CsvUploadRequest;
import com.example.myapp.dto.QuestionDTO;
import com.example.myapp.dto.ManualQuestionRequest;
import com.example.myapp.dto.ProblemGenerationRequest;
import com.example.myapp.model.CustomUserDetails;
import com.example.myapp.model.User;
import com.example.myapp.model.Collection;
import com.example.myapp.model.CollectionSet;
import com.example.myapp.model.Question;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.myapp.config.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;

@RestController
@RequestMapping("/api/generate")
public class QuestionGenerationController {

    private static final Logger log = LoggerFactory.getLogger(QuestionGenerationController.class);

    private final GeminiService geminiService;
    private final CollectionSetService collectionSetService;
    private final QuestionRepository questionRepository;
    private final CollectionRepository collectionRepository;
    private final CollectionService collectionService;
    private final WebSocketHandler webSocketHandler;
    private final Executor asyncExecutor;

    @Autowired
    public QuestionGenerationController(GeminiService geminiService,
            CollectionSetService collectionSetService, QuestionRepository questionRepository,
            CollectionRepository collectionRepository, CollectionService collectionService,
            WebSocketHandler webSocketHandler, Executor asyncExecutor) {
        this.geminiService = geminiService;
        this.collectionSetService = collectionSetService;
        this.questionRepository = questionRepository;
        this.collectionRepository = collectionRepository;
        this.collectionService = collectionService;
        this.webSocketHandler = webSocketHandler;
        this.asyncExecutor = asyncExecutor; // 非同期処理用の Executor を注入
    }

    @PostMapping("/ai")
    public ResponseEntity<Void> generateQuestions(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody AiQuestionRequest request) {

        User user = customUserDetails.getUser();
        CollectionSet collectionSet = collectionSetService.createCollectionSet(user,
                request.getCollectionSetName(), request.isPublic());
        Collection collection =
                collectionService.createCollection(collectionSet, request.getTheme(), true);

        // 非同期処理を独自の Executor で実行
        CompletableFuture.runAsync(() -> {
            try {
                // GeminiService.generateQuestions は CompletableFuture<List<QuestionDTO>> を返す
                List<Question> questions = geminiService.generateQuestions(request.getTheme(),
                        request.getQuestion_format(), request.getAnswer_format(),
                        request.getQuestion_example(), request.getAnswer_example(), collection)
                        .get();
                questionRepository.saveAll(questions);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("collectionId", collection.getId());

                String jsonResponse = new ObjectMapper().writeValueAsString(response);
                webSocketHandler.sendMessageToClients(jsonResponse);

            } catch (Exception e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", e.getMessage());
                try {
                    String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
                    webSocketHandler.sendMessageToClients(jsonResponse);
                } catch (Exception jsonException) {
                    log.error("Error serializing error response", jsonException);
                }
            }
        }, asyncExecutor);

        // 即時に 202 Accepted を返す
        return ResponseEntity.accepted().build();
    }
}
