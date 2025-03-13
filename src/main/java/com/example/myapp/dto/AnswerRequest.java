package com.example.myapp.dto;

public class AnswerRequest {
    private Long questionId;
    private String userAnswer;

    // コンストラクタ
    public AnswerRequest() {}

    public AnswerRequest(Long questionId, String userAnswer) {
        this.questionId = questionId;
        this.userAnswer = userAnswer;
    }

    // ゲッターとセッター
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}
