package com.example.controller;

import com.example.dao.ProblemRepository;
import com.example.model.Problem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}
