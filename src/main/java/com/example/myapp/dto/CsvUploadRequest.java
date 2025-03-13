package com.example.myapp.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CsvUploadRequest {
    private MultipartFile file;
    private String collectionSetName;
    private String collectionName;
    private boolean isPublic;
}
