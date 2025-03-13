package com.example.myapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;

import java.util.List;
import com.example.myapp.model.Question;
import com.example.myapp.service.QuestionService;
import com.example.myapp.dto.QuestionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // id で問題を取得
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(new QuestionDTO(question));
    }

    @PostMapping("/{id}/check")
    public ResponseEntity<AnswerResponse> checkAnswer(@PathVariable Long id,
            @RequestBody AnswerRequest request) {
        AnswerResponse answer = questionService.checkAnswer(id, request.getUserAnswer());
        return ResponseEntity.ok(answer);
    }

    public static class AnswerRequest {
        private String userAnswer;

        public String getUserAnswer() {
            return userAnswer;
        }

        public void setUserAnswer(String userAnswer) {
            this.userAnswer = userAnswer;
        }
    }

    public static class AnswerResponse {
        private boolean isCorrect;
        private String correctAnswer;
        private String description;

        public AnswerResponse(boolean isCorrect, String correctAnswer, String description) {
            this.isCorrect = isCorrect;
            this.correctAnswer = correctAnswer;
            this.description = description;
        }

        public boolean getIsCorrect() {
            return isCorrect;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public String getDescription() {
            return description;
        }
    }

    @GetMapping
    public ResponseEntity<List<Long>> getQuestionIds(@RequestParam List<Long> collectionIds,
            @RequestParam String order, @RequestParam int limit) {
        List<Long> questionIds = questionService.getQuestionIds(collectionIds, order, limit);
        return ResponseEntity.ok(questionIds);
    }

    @GetMapping("/hint/{questionId}")
    public ResponseEntity<String> getNextHint(@PathVariable Long questionId,
            @RequestParam int index) { // クエリパラメータで取得すべき文字のインデックスを指定
        String nextHintChar = questionService.getNextHintChar(questionId, index);
        return ResponseEntity.ok(nextHintChar);
    }

    @GetMapping("/collections/{collectionId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByCollectionId(
            @PathVariable Long collectionId) {
        List<Question> questions = questionService.getQuestionsByCollectionId(collectionId);
        List<QuestionDTO> questionDTOs = questionService.convertQuestionDTOs(questions);
        return ResponseEntity.ok(questionDTOs);
    }

    @PutMapping("/batch")
    public ResponseEntity<Void> updateQuestions(@RequestBody List<Question> questions) {
        questionService.updateQuestions(questions);
        return ResponseEntity.noContent().build(); // 204 No Content を返す
    }
}
