package com.example.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.storage.UserSessionMap;

@Configuration
public class UserSessionConfig {
    @Bean
    public UserSessionMap myUserSessionMap() {
        UserSessionMap userSessionMap = new UserSessionMap();
        return userSessionMap;
    }
}
