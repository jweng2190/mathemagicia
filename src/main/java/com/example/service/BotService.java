package com.example.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.example.model.Bot;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BotService {
    private List<Bot> bots;

    @PostConstruct
    public void init() {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            InputStream inputStream = getClass().getResourceAsStream("bot.json");
            List<Bot> bots = objectMapper.readValue(inputStream, new TypeReference<List<Bot>>() {});
            this.bots = bots;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getBotTimes(int level) {
        Optional<Bot> bot = bots.stream().filter(b -> b.getLevel() == level).findFirst();
    }
}
