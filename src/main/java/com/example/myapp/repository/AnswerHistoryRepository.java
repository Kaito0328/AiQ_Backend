package com.example.myapp.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.myapp.model.Question;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.myapp.model.AnswerHistory;
import com.example.myapp.model.User;

@Repository
public interface AnswerHistoryRepository extends JpaRepository<AnswerHistory, Long> {
    List<AnswerHistory> findByUser(User user);
}
