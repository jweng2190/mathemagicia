package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.example.model.DifficultyLevel;

import com.example.service.DifficultyLevelService;

@RestController
@RequestMapping("/difficulty")
public class DifficultyController {
    @Autowired
    private final DifficultyLevelService difficultyLevelService;

    public DifficultyController(DifficultyLevelService difficultyLevelService) {
        this.difficultyLevelService = difficultyLevelService;
    }


    @GetMapping("/levels")
    public ResponseEntity<List<DifficultyLevel>> getDifficultyLevels() {
        List<DifficultyLevel> levels = difficultyLevelService.getDifficultyLevels();
        return ResponseEntity.ok().body(levels);
    }
}
