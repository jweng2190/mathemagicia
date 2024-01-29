package com.example.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import com.example.dao.ProblemRepository;
import com.example.model.Problem;
import com.example.model.Game;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProblemService {
    @Autowired
    private ProblemRepository problemDao;
    public static final int PROBLEM_SET_SIZE = 20;

    public List<Problem> getRandomProblems(Game game) {
        String difficulty = game.getGameDifficulty();
        List<Problem> allProblems;

        if(difficulty == null) {
            allProblems = problemDao.findRandProblems(PROBLEM_SET_SIZE);
            return allProblems;
        }

        if(difficulty.equals("easy")) {
            allProblems = problemDao.findAllEasyProblems(PROBLEM_SET_SIZE);
        } else if(difficulty.equals("medium")) {
            allProblems = problemDao.findAllMediumProblems(PROBLEM_SET_SIZE);
        } else if(difficulty.equals("hard")) {
            allProblems = problemDao.findAllHardProblems(PROBLEM_SET_SIZE);
        } else {
            allProblems = problemDao.findAllMediumProblems(PROBLEM_SET_SIZE);
        }

        return allProblems;
    }
}
