package com.example.service;

import com.example.model.XpLevel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class XpLevelService {
    private List<XpLevel> xpLevels;

    @PostConstruct
    public void init() {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            InputStream inputStream = getClass().getResourceAsStream("level.json");
            List<XpLevel> xpLevels = objectMapper.readValue(inputStream, new TypeReference<List<XpLevel>>() {});
            this.xpLevels = xpLevels;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getXpToLevelUp(int levelValue) {
        Optional<XpLevel> matchingLevel = xpLevels.stream()
                .filter(level -> level.getLevel() == levelValue)
                .findFirst();

        return matchingLevel.map(XpLevel::getXpLevelUp).orElse(0);
    }

    public List<Integer> levelUp(int currentXp, int currentLevel, int xpAdd) {
        int xpLevel = getXpToLevelUp(currentLevel);
        int totalXp = currentXp + xpAdd;
        while(totalXp >= xpLevel) {
            totalXp -= xpLevel;
            if(currentLevel == 25) {
                return Arrays.asList(-1, 0);
            }
            currentLevel++;
            xpLevel = getXpToLevelUp(currentLevel);
        }

        return Arrays.asList(currentLevel, totalXp);
    }
}
