package com.example.controller;

import javax.annotation.security.RolesAllowed;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.example.dao.GameRepository;
import com.example.dao.UserRepository;
import com.example.model.Game;
import com.example.model.User;
import com.example.service.XpLevelService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/xp")
public class XpController {
    private XpLevelService xpLevelService;
    private UserRepository userDao;
    private GameRepository gameDao;

    @PostMapping(path = "/level_up", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<List<List<Integer>>> getXpStats(@RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        String playerType = payload.get("playerType");

        Game game = gameDao.getGameByGameId(gameId);
        String username;
        int xpAdd = 0;
        if(playerType.equals("1")) {
            username = game.getPlayer1Username();
            xpAdd = game.getPlayer1Xp();
        } else if(playerType.equals("2")) {
            username = game.getPlayer2Username();
            xpAdd = game.getPlayer2Xp();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
        User user = userDao.getUserByUsername(username);
        int prevLevel = user.getLevel();
        int prevXp = user.getXp();
        int prevXpLevel = xpLevelService.getXpToLevelUp(prevLevel);

        List<Integer> oldXpInfo = Arrays.asList(prevLevel, prevXp, prevXpLevel);
        List<Integer> xpInfo = xpLevelService.levelUp(prevXp, prevLevel, xpAdd);

        user.setLevel(xpInfo.get(0));
        user.setXp(xpInfo.get(1));
        userDao.save(user);
        
        return ResponseEntity.ok().body(Arrays.asList(oldXpInfo, xpInfo, Arrays.asList(xpAdd)));
    }
}
