package com.example.myapp.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import com.example.myapp.model.Collection;
import com.example.myapp.model.Question;
import com.example.myapp.dto.CsvUploadRequest;
import com.example.myapp.service.CsvUploadService;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.myapp.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class CsvUploadService {

    public List<Question> parseCsvFile(MultipartFile file, Collection collection) {
        List<Question> questions = new ArrayList<>();
        try {
            // ファイルの内容を文字列として読み込む
            String content = new String(file.getBytes());
            String[] lines = content.split("\n");

            boolean hasHeader = false;
            String[] headers = null;
            int questionIndex = 0, answerIndex = 1, descriptionIndex = -1; // descriptionIndex 初期値を
                                                                           // -1 に設定

            // 1行目がヘッダーかどうかを判定
            if (lines.length > 0 && lines[0].toLowerCase().contains("question")
                    && lines[0].toLowerCase().contains("answer")) {
                hasHeader = true;
                headers = lines[0].split(",");

                // ヘッダーがある場合、インデックスを動的に設定
                for (int i = 0; i < headers.length; i++) {
                    String header = headers[i].trim().toLowerCase();
                    if (header.equals("question")) {
                        questionIndex = i;
                    } else if (header.equals("answer")) {
                        answerIndex = i;
                    } else if (header.equals("description")) {
                        descriptionIndex = i;
                    }
                }
            }

            // ヘッダーがある場合、1行目をスキップ
            int startLine = hasHeader ? 1 : 0;

            // 行を処理
            for (int i = startLine; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty())
                    continue; // 空行をスキップ

                String[] columns = line.split(",");
                if (columns.length >= 2) { // 少なくとも question と answer は必要
                    Question question = new Question();
                    question.setCollection(collection);

                    // 動的にカラムを割り当てる
                    question.setQuestionText(columns[questionIndex].trim());
                    question.setCorrectAnswer(columns[answerIndex].trim());

                    // description がある場合のみ設定
                    if (descriptionIndex != -1 && descriptionIndex < columns.length) {
                        question.setDescriptionText(columns[descriptionIndex].trim());
                    } else {
                        question.setDescriptionText(null); // ない場合は null を設定
                    }

                    questions.add(question);
                } else {
                    // 必要なカラムが足りない場合はエラーを出力
                    System.out.println("不正な行: " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return questions;
    }
}
