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
    public static final int NUM_BOT_PROBS = 10;

    public List<Problem> getRandomProblems(Game game) {
        String difficulty = game.getGameDifficulty();
        int numProbs = getProblemSetSize(game.getTimeLimit());
        List<Problem> allProblems;

        if(difficulty == null) {
            allProblems = problemDao.findRandProblems(numProbs);
            return allProblems;
        }

        if(difficulty.equals("easy")) {
            allProblems = problemDao.findAllEasyProblems(numProbs);
        } else if(difficulty.equals("medium")) {
            allProblems = problemDao.findAllMediumProblems(numProbs);
        } else if(difficulty.equals("hard")) {
            allProblems = problemDao.findAllHardProblems(numProbs);
        } else {
            allProblems = problemDao.findAllMediumProblems(numProbs);
        }

        return allProblems;
    }

    public int getProblemSetSize(String timeStr) {
        int pSetSize;
        if(timeStr.equals("5:00")) {
            pSetSize = 5;
        } else if(timeStr.equals("10:00")) {
            pSetSize = 10;
        } else if(timeStr.equals("20:00")) {
            pSetSize = 20;
        } else {
            pSetSize = 10;
        }
        return pSetSize;
    }
}
