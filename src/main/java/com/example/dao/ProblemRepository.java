package com.example.dao;

import com.example.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {
    @Query(value = "SELECT * FROM problem WHERE contest=?1", nativeQuery = true)
    List<Problem> findProblemByContest(String contestName);

    @Query(value = "SELECT answer FROM problem WHERE problem_id=?1", nativeQuery = true)
    String findAnswerByProblem(Integer problemId);

    @Query(value = "SELECT difficulty FROM problem WHERE problem_id=?1", nativeQuery = true)
    Integer findDifficultyByProblemId(Integer problemId);

    @Query(value = "SELECT * FROM problem WHERE difficulty BETWEEN 1 AND 3 ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Problem> findAllEasyProblems(Integer maxProbs);

    @Query(value = "SELECT * FROM problem WHERE difficulty BETWEEN 4 AND 6 ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Problem> findAllMediumProblems(Integer maxProbs);

    @Query(value = "SELECT * FROM problem WHERE difficulty BETWEEN 7 AND 10 ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Problem> findAllHardProblems(Integer maxProbs);

    @Query(value = "SELECT * FROM problem WHERE difficulty BETWEEN 1 AND 10 ORDER BY RAND() LIMIT ?1", nativeQuery = true)
    List<Problem> findRandProblems(Integer maxProbs);
}
