package com.example.myapp.dto;

import com.example.myapp.model.Question;
import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;
    private String questionText; // 問題文
    private String correctAnswer; // 正解
    private String descriptionText; // 説明文

    public QuestionDTO(String questionText, String correctAnswer, String descriptionText) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.descriptionText = descriptionText;
    }

    public QuestionDTO() {}

    public QuestionDTO(Question question) {
        this.id = question.getId();
        this.questionText = question.getQuestionText();
        this.correctAnswer = question.getCorrectAnswer();
        this.descriptionText = question.getDescriptionText();
    }
}
