package com.example.myapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.myapp.model.CustomUserDetails;
import com.example.myapp.model.User;
import com.example.myapp.service.UserService;
import com.example.myapp.model.CustomUserDetails;
import com.example.myapp.service.AnswerHistoryService;
import java.util.List;
import com.example.myapp.model.AnswerHistory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.myapp.dto.AnswerRequest;
import com.example.myapp.service.QuestionService;
import com.example.myapp.model.Question;


@RestController
@RequestMapping("/api/answers")
public class AnswerHistoryController {

    @Autowired
    private AnswerHistoryService answerHistoryService;


    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @PostMapping("/save")
    public ResponseEntity<String> saveAnswer(@RequestBody AnswerRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        Question question = questionService.getQuestionById(request.getQuestionId());

        boolean isCorrect = question.getCorrectAnswer().equals(request.getUserAnswer());
        answerHistoryService.saveAnswer(user, question, request.getUserAnswer(), isCorrect);

        return ResponseEntity.ok("Answer saved successfully");
    }

    @GetMapping("/history")
    public ResponseEntity<List<AnswerHistory>> getUserAnswerHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<AnswerHistory> history = answerHistoryService.getUserAnswerHistory(user);
        return ResponseEntity.ok(history);
    }
}
