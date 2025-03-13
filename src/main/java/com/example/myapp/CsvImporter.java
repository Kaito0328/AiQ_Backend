package com.example.myapp;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.EmptyResultDataAccessException;
import java.nio.file.Files;
import com.example.myapp.model.User;
import com.example.myapp.service.UserService;

@Component
public class CsvImporter {

    private final JdbcTemplate jdbcTemplate;
    private static final String DATA_PATH = "src/main/resources/data/";
    private static final String DATA_DIRECTORY = "/data";

    public CsvImporter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void importCsvFiles() throws IOException {
        File dataRoot = new File(DATA_PATH);
        File[] collectionSetDirs = dataRoot.listFiles(File::isDirectory);

        if (collectionSetDirs != null) {
            for (File collectionSetDir : collectionSetDirs) {
                String collectionSetName = collectionSetDir.getName();
                int collectionSetId = insertOrGetCollectionSetId(collectionSetName);

                File[] csvFiles = collectionSetDir.listFiles((dir, name) -> name.endsWith(".csv"));
                if (csvFiles != null) {
                    for (File csvFile : csvFiles) {
                        String collectionName = csvFile.getName().replace(".csv", "");
                        int collectionId = insertCollection(collectionSetId, collectionName);

                        // バッチ処理を使って複数のINSERTを一度に実行
                        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                            String line;
                            br.readLine(); // ヘッダー行を読み飛ばす

                            // バッチ処理用リスト
                            List<Object[]> batchArgs = new ArrayList<>();

                            while ((line = br.readLine()) != null) {
                                String[] columns = line.split(",");
                                if (columns.length >= 2) {
                                    String questionText = columns[0];
                                    String correctAnswer = columns[1];
                                    String description = (columns.length > 2) ? columns[2] : null;

                                    // バッチ用の引数リストに追加
                                    batchArgs.add(new Object[] {collectionId, questionText,
                                            correctAnswer, description});
                                }
                            }

                            // バッチインサートを実行
                            String insertSql =
                                    "INSERT INTO questions (collection_id, question_text, correct_answer, description_text) "
                                            + "VALUES (?, ?, ?, ?)";
                            jdbcTemplate.batchUpdate(insertSql, batchArgs);
                        }
                    }
                }
            }
        }
    }

    private Integer getCollectionSetId(String collectionSetName) {
        try {
            return jdbcTemplate.queryForObject("SELECT id FROM collection_sets WHERE name = ?",
                    Integer.class, collectionSetName);
        } catch (EmptyResultDataAccessException ex) {
            return null; // レコードが存在しない場合は null を返す
        }
    }

    private int insertOrGetCollectionSetId(String collectionSetName) {
        Integer id = getCollectionSetId(collectionSetName);
        if (id == null) {
            jdbcTemplate.update(
                    "INSERT INTO collection_sets (name, user_id, is_public) VALUES (?, ?, ?)",
                    collectionSetName, UserService.getOfficialUserId(), true);
            id = getCollectionSetId(collectionSetName);
        }
        return id;
    }

    private Integer getCollectionId(int collectionSetId, String collectionName) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id FROM collections WHERE collection_set_id = ? AND name = ?",
                    new Object[] {collectionSetId, collectionName}, Integer.class);
        } catch (EmptyResultDataAccessException ex) {
            return null; // レコードが存在しない場合は null を返す
        }
    }

    private int insertCollection(int collectionSetId, String collectionName) {
        Integer id = getCollectionId(collectionSetId, collectionName);
        if (id == null) {
            jdbcTemplate.update(
                    "INSERT INTO collections (collection_set_id, name, generated_by_ai) VALUES (?, ?, ?)",
                    collectionSetId, collectionName, false);
            id = getCollectionId(collectionSetId, collectionName);
        }
        return id;
    }
}
