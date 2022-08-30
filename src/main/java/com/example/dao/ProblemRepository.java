package com.example.dao;

import com.example.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {
    @Query(value = "SELECT * FROM problem WHERE contest=?1", nativeQuery = true)
    List<Problem> findProblemByContest(String contestName);
}
