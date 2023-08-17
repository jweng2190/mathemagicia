package com.example.service;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.example.model.DifficultyLevel;
import com.example.wrapper.DifficultyLevelsWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Service
public class DifficultyLevelService {
    private List<DifficultyLevel> difficultyLevels;

    @PostConstruct
    public void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStream inputStream = getClass().getResourceAsStream("difficulty_xp.json");
            DifficultyLevelsWrapper wrapper = objectMapper.readValue(inputStream, DifficultyLevelsWrapper.class);
            difficultyLevels = wrapper.getDifficultyLevels();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getXpForDifficultyLevel(int levelValue) {
        Optional<DifficultyLevel> matchingLevel = difficultyLevels.stream()
                .filter(level -> level.getLevel() == levelValue)
                .findFirst();

        return matchingLevel.map(DifficultyLevel::getXpToAdd).orElse(0);
    }

    public List<DifficultyLevel> getDifficultyLevels() {
        return difficultyLevels;
    }  
}
