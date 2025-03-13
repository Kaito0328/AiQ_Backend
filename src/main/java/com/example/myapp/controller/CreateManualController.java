package com.example.myapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.myapp.service.CollectionService;
import com.example.myapp.service.CollectionSetService;
import com.example.myapp.service.QuestionService;
import com.example.myapp.repository.QuestionRepository;
import com.example.myapp.dto.ManualQuestionRequest;
import com.example.myapp.model.CustomUserDetails;
import com.example.myapp.model.User;
import com.example.myapp.model.Collection;
import com.example.myapp.model.CollectionSet;
import com.example.myapp.model.Question;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.myapp.config.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@RestController
@RequestMapping("/api/generate")
public class CreateManualController {

    private static final Logger log = LoggerFactory.getLogger(CreateManualController.class);

    private final CollectionService collectionService;
    private final CollectionSetService collectionSetService;
    private final QuestionService questionService;
    private final WebSocketHandler webSocketHandler;
    private final Executor asyncExecutor;
    private final QuestionRepository questionRepository;

    @Autowired
    public CreateManualController(CollectionSetService collectionSetService,
            CollectionService collectionService, QuestionService questionService,
            WebSocketHandler webSocketHandler, Executor asyncExecutor,
            QuestionRepository questionRepository) {
        this.collectionSetService = collectionSetService;
        this.collectionService = collectionService;
        this.questionService = questionService;
        this.webSocketHandler = webSocketHandler;
        this.asyncExecutor = asyncExecutor;
        this.questionRepository = questionRepository;
    }

    @PostMapping("/manual")
    public ResponseEntity<Map<String, Object>> createManualQuestions(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ManualQuestionRequest request) {

        User user = customUserDetails.getUser();
        CollectionSet collectionSet = collectionSetService.createCollectionSet(user,
                request.getCollectionSetName(), request.isPublic());
        Collection collection = collectionService.createCollection(collectionSet,
                request.getCollectionName(), false);

        Map<String, Object> response = new HashMap<>();
        try {
            // 同期処理で問題を解析
            List<Question> questions =
                    questionService.parseQuestionDTOs(request.getQuestions(), collection);

            // 問題をデータベースに保存
            questionRepository.saveAll(questions);

            // 成功レスポンスを作成
            response.put("success", true);
            response.put("collectionId", collection.getId());

        } catch (Exception e) {
            // エラーレスポンスを作成
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        // 処理結果をレスポンスとして返す
        return ResponseEntity.accepted().body(response);
    }


}
