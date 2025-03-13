package com.example.myapp.dto;

import java.util.List;
import lombok.Data;
import com.example.myapp.dto.QuestionDTO;

@Data
public class ManualQuestionRequest {
    private String collectionSetName; // コレクションセット名
    private String collectionName; // コレクション名
    private List<QuestionDTO> questions; // 問題のリスト
    private boolean isPublic;
}

