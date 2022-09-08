package com.example.controller;

import com.example.dao.ProblemRepository;
import com.example.model.Problem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProblemController {
    @Autowired
    private ProblemRepository problemDao;

    @GetMapping("/problems")
    public ResponseEntity<List<Problem>> getAllProblems() {
        List<Problem> allProblems = problemDao.findAll();
        return ResponseEntity.ok().body(allProblems);
    }

    @GetMapping("/mathcounts")
    public ResponseEntity<List<Problem>> getMathcountsProblems() {
        List<Problem> mathcountsProblems = problemDao.findProblemByContest("mathcounts");
        return ResponseEntity.ok().body(mathcountsProblems);
    }

    @GetMapping("/amc8")
    public ResponseEntity<List<Problem>> getAMC8Problems() {
        List<Problem> amc8Problems = problemDao.findProblemByContest("amc8");
        return ResponseEntity.ok().body(amc8Problems);
    }

    @GetMapping("/answer")
    public String getAnswer(@RequestParam(name="problemId") Integer problemId) {
        String problemAnswer = problemDao.findAnswerByProblem(problemId);
        return problemAnswer;
    }
}
