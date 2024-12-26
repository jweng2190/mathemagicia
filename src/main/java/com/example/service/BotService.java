package com.example.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.example.model.Bot;
import com.example.model.Problem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BotService {
    private List<Bot> bots;
    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void init() {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            final Resource fileResource = resourceLoader.getResource("classpath:bot.json");
            InputStream inputStream = fileResource.getInputStream();
            List<Bot> bots = objectMapper.readValue(inputStream, new TypeReference<List<Bot>>() {});
            this.bots = bots;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Object> getBotTimes(int level, int timeMins, List<Problem> problemSet) {
        Optional<Bot> optionalBot = bots.stream().filter(b -> b.getLevel() == level).findFirst();
        Bot bot = optionalBot.orElseThrow(() -> new NoSuchElementException("Error: no bot found!"));

        int timeSecs = timeMins * 60;
        List<Integer> answerTimes = new ArrayList<>();
        int time = 0;
        int botScore = 0;
        Random random = new Random();

        for(int i = 0, k = problemSet.size(); i < k; i++) {
            int difficulty = problemSet.get(i).getDifficulty();
            int originalTime = bot.findTimeByDiff(difficulty);
            double randomNumber = -1 + (random.nextDouble() * 2);
            int delta = (int) (bot.findErrorByDiff(difficulty) * randomNumber);
            int finalTime = originalTime + delta;
            answerTimes.add(finalTime);
            if(time + finalTime > timeSecs) {
                break;
            }
            time += finalTime;
            botScore++;
        }

        return Arrays.asList(botScore, answerTimes);
    }
}
