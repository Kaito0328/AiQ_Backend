package com.example.myapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.example.myapp.model.Collection;
import com.example.myapp.model.Question;
import com.example.myapp.repository.QuestionRepository;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.myapp.controller.QuestionController.AnswerResponse;
import com.example.myapp.dto.CollectionDTO;
import com.example.myapp.dto.QuestionDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;



@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }


    public List<QuestionDTO> convertQuestionDTOs(List<Question> questions) {
        return questions.stream().map(QuestionDTO::new).collect(Collectors.toList());
    }

    public List<Question> parseQuestionDTOs(List<QuestionDTO> questionDTOs, Collection collection) {
        List<Question> questions = new ArrayList<>();
        for (QuestionDTO dto : questionDTOs) {
            Question question = new Question();
            question.setCollection(collection);
            question.setQuestionText(dto.getQuestionText());
            question.setCorrectAnswer(dto.getCorrectAnswer());
            question.setDescriptionText(dto.getDescriptionText());
            questions.add(question);
        }
        return questions;
    }

    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(
                () -> new RuntimeException("Question not found with id: " + questionId));
    }

    private boolean isCorrect(Question question, String userAnswer) {
        // 正解の選択肢を "/" で分割する
        String[] correctAnswers = question.getCorrectAnswer().split("/");

        // 各正解選択肢とユーザーの回答を比較する
        for (String correctAnswer : correctAnswers) {
            if (correctAnswer.trim().equalsIgnoreCase(userAnswer.trim())) {
                return true; // 一致したら正解
            }
        }

        // 一致しなければ不正解
        return false;
    }

    public AnswerResponse checkAnswer(Long id, String userAnswer) {
        Question question = getQuestionById(id);
        return new AnswerResponse(isCorrect(question, userAnswer), question.getCorrectAnswer(),
                question.getDescriptionText());
    }

    private List<Long> questionsToIds(List<Question> questions) {
        // 質問IDのリストを返す
        return questions.stream().map(Question::getId).collect(Collectors.toList());
    }

    private List<Question> getQuestions(List<Long> collectionIds, String order, int limit) {
        Pageable pageable = PageRequest.of(0, limit); // ページ番号0、limit件数

        List<Question> questions;

        // 並び順に応じてクエリを分ける
        switch (order.toLowerCase()) {
            case "asc":
                questions = questionRepository
                        .findByCollectionIdInOrderByIdAscWithLimit(collectionIds, pageable);
                break;
            case "desc":
                questions = questionRepository
                        .findByCollectionIdInOrderByIdDescWithLimit(collectionIds, pageable);
                break;
            case "random":
                questions = questionRepository
                        .findByCollectionIdInOrderByRandomWithLimit(collectionIds, pageable);
                break;
            default:
                throw new IllegalArgumentException("Invalid order type");
        }

        return questions;
    }

    public List<Long> getQuestionIds(List<Long> collectionIds, String order, int limit) {
        List<Question> questions = getQuestions(collectionIds, order, limit);
        return questionsToIds(questions);
    }

    public String getNextHintChar(Long questionId, int index) {
        Question question = getQuestionById(questionId);
        String answer = question.getCorrectAnswer();

        if (index >= answer.length()) {
            return ""; // すべてのヒントを表示し終えたら空文字を返す
        }

        return String.valueOf(answer.charAt(index));
    }

    public List<Question> getQuestionsByCollectionId(Long collectionId) {
        List<Question> questions = questionRepository.findByCollectionId(collectionId);
        return questions;
    }

    @Transactional
    public List<Question> updateQuestions(List<Question> questions) {
        List<Question> updatedQuestions = new ArrayList<>();

        for (Question requestQuestion : questions) {
            Question existingQuestion = questionRepository.findById(requestQuestion.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Question not found with ID: " + requestQuestion.getId()));

            if (requestQuestion.getQuestionText() != null) {
                existingQuestion.setQuestionText(requestQuestion.getQuestionText());
            }
            if (requestQuestion.getCorrectAnswer() != null) {
                existingQuestion.setCorrectAnswer(requestQuestion.getCorrectAnswer());
            }

            if (requestQuestion.getDescriptionText() != null) {
                existingQuestion.setDescriptionText(requestQuestion.getDescriptionText());
            }

            updatedQuestions.add(existingQuestion);
        }

        return questionRepository.saveAll(updatedQuestions);
    }

    // // 全ての質問をDTOで返す
    // public List<QuestionDTO> getAllQuestions() {
    // return questionRepository.findAll().stream()
    // .map(question -> new QuestionDTO(question.getQuestionText(),
    // question.getCorrectAnswer(), question.getDescriptionText()))
    // .collect(Collectors.toList());
    // }

    // // quizId で質問をDTOで返す
    // public QuestionResponse getQuestionById(Long id) {
    // Optional<Question> questionOpt = questionRepository.findById(id);
    // if (questionOpt.isPresent()) {
    // Question question = questionOpt.get();
    // String questionText = question.getQuestionText();
    // String correctionName = question.getCollection().getName();
    // return new QuestionResponse(questionText, correctionName);
    // } else {
    // throw new RuntimeException("Question not found");
    // }
    // }
}
