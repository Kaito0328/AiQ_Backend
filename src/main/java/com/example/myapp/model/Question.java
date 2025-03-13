package com.example.myapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import com.example.myapp.model.Collection;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @Column(nullable = false)
    private String question_text;

    @Column(nullable = false)
    private String correct_answer;

    @Column
    private String description_text;

    // getter と setter を追加

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public String getQuestionText() {
        return question_text;
    }

    public void setQuestionText(String question_text) {
        this.question_text = question_text;
    }

    public String getCorrectAnswer() {
        return correct_answer;
    }

    public void setCorrectAnswer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    public String getDescriptionText() {
        return description_text;
    }

    public void setDescriptionText(String description_text) {
        this.description_text = description_text;
    }
}
