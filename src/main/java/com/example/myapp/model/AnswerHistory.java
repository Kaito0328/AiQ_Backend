package com.example.myapp.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "answer_history")
public class AnswerHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // ユーザーとのリレーション

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // 問題とのリレーション

    @Column(name = "user_answer")
    private String userAnswer; // ユーザーの回答

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect; // 正誤判定

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt; // 回答日時

    // 🔹 コンストラクタ
    public AnswerHistory() {}

    public AnswerHistory(User user, Question question, String userAnswer, boolean isCorrect) {
        this.user = user;
        this.question = question;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
    }

    // 🔹 Getter & Setter

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

