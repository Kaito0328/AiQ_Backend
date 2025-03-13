package com.example.myapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.myapp.model.CustomUserDetails;
import com.example.myapp.model.User;
import com.example.myapp.repository.QuestionRepository;
import com.example.myapp.model.Collection;
import com.example.myapp.model.CollectionSet;
import com.example.myapp.model.Question;
import com.example.myapp.service.CollectionService;
import com.example.myapp.service.CollectionSetService;
import com.example.myapp.service.CsvUploadService;
import com.example.myapp.service.QuestionService;
import com.example.myapp.config.WebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import com.example.myapp.dto.CsvUploadRequest;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/generate")
public class CsvUploadController {

    private static final Logger log = LoggerFactory.getLogger(CsvUploadController.class);

    private final CollectionService collectionService;
    private final CollectionSetService collectionSetService;
    private final CsvUploadService csvUploadService;
    private final WebSocketHandler webSocketHandler;
    private final Executor asyncExecutor;
    private final QuestionRepository questionRepository;

    @Autowired
    public CsvUploadController(CollectionSetService collectionSetService,
            CollectionService collectionService, CsvUploadService csvUploadService,
            WebSocketHandler webSocketHandler, Executor asyncExecutor,
            QuestionRepository questionRepository) {
        this.collectionSetService = collectionSetService;
        this.collectionService = collectionService;
        this.csvUploadService = csvUploadService;
        this.webSocketHandler = webSocketHandler;
        this.asyncExecutor = asyncExecutor;
        this.questionRepository = questionRepository;
    }

    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadCsv(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart("file") MultipartFile file,
            @RequestParam("collectionSetName") String collectionSetName,
            @RequestParam(value = "collectionName", required = false) String collectionName,
            @RequestParam("public") boolean isPublic) {

        User user = customUserDetails.getUser();
        CollectionSet collectionSet =
                collectionSetService.createCollectionSet(user, collectionSetName, isPublic);

        if (collectionName == null) {
            String fname = file.getOriginalFilename();
            if (fname.contains(".")) {
                fname = fname.substring(0, fname.lastIndexOf(".")); // 拡張子を除去
            }
            collectionName = fname;
        }

        Collection collection =
                collectionService.createCollection(collectionSet, collectionName, false);

        try {
            List<Question> questions = csvUploadService.parseCsvFile(file, collection);
            questionRepository.saveAll(questions);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("collectionId", collection.getId());

            return ResponseEntity.accepted().body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

}
