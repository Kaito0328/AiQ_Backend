package com.example.myapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.myapp.dto.QuestionDTO;
import com.example.myapp.model.Question;
import org.springframework.scheduling.annotation.Async;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import com.example.myapp.model.Collection;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public CompletableFuture<List<Question>> generateQuestions(String theme, String questionFormat,
            String answerFormat, String questionExample, String answerExample,
            Collection collection) {
        String prompt = "テーマ: " + theme + "\\n問題のフォーマット: " + questionFormat + "(例: "
                + questionExample + ")\\n" + "回答のフォーマット: " + answerFormat + "(例: " + answerExample
                + ")\\n" + "以下の JSON 形式で100問生成してください:\\n"
                + "{ \"questions\": [ { \"question\": \"問題文\", \"answer\": \"解答\", \"description\": \"補足知識(50文字程度)\"}, ... ] }";

        System.out.println(prompt);

        String apiUrl = UriComponentsBuilder.fromHttpUrl(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent")
                .queryParam("key", apiKey).toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        String requestBody = "{" + "\"contents\": [{" + "\"parts\": [{" + "\"text\": \""
                + prompt.replace("\"", "\\\"") + "\"" + "}]" + "}]" + "}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            System.out.println(response);
            return CompletableFuture
                    .completedFuture(parseQuestions(response.getBody(), collection));
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
    }

    private List<Question> parseQuestions(String responseBody, Collection collection) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            String rawJson = jsonNode.get("candidates").get(0).get("content").get("parts").get(0)
                    .get("text").asText();

            // もし JSON がコードブロックとして囲まれている場合、それを除去
            String cleanedJson = rawJson.replaceAll("```json", "").replaceAll("```", "").trim();
            JsonNode parsedJson = objectMapper.readTree(cleanedJson);

            List<Question> questions = new ArrayList<>();
            for (JsonNode node : parsedJson.get("questions")) {
                Question question = new Question();
                question.setQuestionText(node.get("question").asText());
                question.setCorrectAnswer(node.get("answer").asText());
                question.setDescriptionText(node.get("description").asText());
                question.setCollection(collection);
                questions.add(question);
            }
            return questions;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
