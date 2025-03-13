package com.example.myapp.dto;

import lombok.Data;

@Data
public class ProblemGenerationRequest {
    private String theme;
    private String question_format;
    private String answer_format;
    private String question_example;
    private String answer_example;
    private String collectionSetName;
}
