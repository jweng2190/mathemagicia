package com.example.controller;

import com.example.dao.ProblemRepository;
import com.example.model.Problem;
import com.example.service.ProblemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class ProblemController {
    @Autowired
    private ProblemRepository problemDao;

    private ProblemService problemService;

    @GetMapping("/all_problems")
    public ResponseEntity<List<Problem>> getAllProblems() {
        List<Problem> allProblems = problemDao.findAll();
        return ResponseEntity.ok().body(allProblems);
    }

    @GetMapping("/problems")
    public ResponseEntity<List<Problem>> getProblemsByContest(@RequestParam(name="contest") String contest) {
        List<Problem> contestProblems = problemDao.findProblemByContest(contest);
        return ResponseEntity.ok().body(contestProblems);
    }

    /* @GetMapping("/problems/random/{number}")
    public ResponseEntity<List<Problem>> getRandomProblems(@PathVariable("number") Integer numProblems) {
        List<Problem> randProblems = problemService.getRandomProblems(numProblems);
        return ResponseEntity.ok().body(randProblems);
    } */

    @GetMapping("/answer")
    public String getAnswer(@RequestParam(name="problemId") Integer problemId) {
        String problemAnswer = problemDao.findAnswerByProblem(problemId);
        return problemAnswer;
    }
}
