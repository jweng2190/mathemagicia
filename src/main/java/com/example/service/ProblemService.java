package com.example.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    public static final int PROBLEM_SET_SIZE = 1;

    public List<Problem> getRandomProblems(Integer numProblems, Game game) {
        String difficulty = game.getGameDifficulty();
        List<Problem> allProblems;
        if(difficulty.equals("easy")) {
            allProblems = problemDao.findAllEasyProblems();
        } else if(difficulty.equals("medium")) {
            allProblems = problemDao.findAllMediumProblems();
        } else if(difficulty.equals("hard")) {
            allProblems = problemDao.findAllHardProblems();
        } else {
            allProblems = problemDao.findAllMediumProblems();
        }
        
        int length = allProblems.size();
        ArrayList<Integer> problemIndices = new ArrayList<Integer>();
        for(int i = 0; i < length; i++) {
            problemIndices.add(i, i);
        }

        Collections.shuffle(problemIndices);

        List<Problem> problemList = new ArrayList<Problem>();
        for(int i = 0; i < numProblems; i++) {
            problemList.add(i, allProblems.get(problemIndices.get(i)));
        }

        return problemList;
    }
}
