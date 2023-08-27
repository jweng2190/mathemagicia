package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dao.GameRepository;
import com.example.dao.UserRepository;
import com.example.model.Game;
import com.example.model.User;

import java.security.Principal;

import javax.annotation.security.RolesAllowed;
import javax.websocket.server.PathParam;

@Controller
public class ViewController {
    @Autowired
    private GameRepository gameDao;
    @Autowired
    private UserRepository userDao;

    @RolesAllowed({"USER"})
    @GetMapping("/view")
    public String renderProblems(@RequestParam(name="contest") String contest,
    @RequestParam(name="problemId") Integer problemId) {
        return "problem_template";
    }

    @RolesAllowed({"USER"})
    @GetMapping("/game/{gameId}")
    public String game(Principal principal, @PathVariable String gameId) {
        String username = principal.getName();
        Game game = gameDao.getGameByGameId(gameId);
        if(game.getPlayer1Username() != null && game.getPlayer2Username() != null) {
            return "game_error";
        }
        User user = userDao.getUserByUsername(username);
        user.setActiveGameId(gameId);
        userDao.save(user);
        
        return "game_template";
    }

    @RolesAllowed({"USER"})
    @GetMapping("/computer/{computerLevel}")
    public String computerGame(Principal principal, @PathVariable("computerLevel") int computerLevel) {
        return "game_computer";
    }


    @RolesAllowed({"USER"})
    @GetMapping("/invite_friend")
    public String inviteFriend() {
        return "invite_friend";
    }

    @RolesAllowed({"USER"})
    @GetMapping("/game_home")
    public String gameHome() {
        return "game";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }

        return "redirect:/home";
    }

    @GetMapping("past_games")
    public String pastGames() {
        return "past_games";
    }

    @GetMapping("play_computer")
    public String playComputer() {
        return "computer";
    }


    @GetMapping("/test_end")
    public String testEnd() {
        return "test_end";
    }

    @GetMapping("/test_latex")
    public String testLatex() {
        return "test_latex";
    }

    @GetMapping("/test_game")
    public String testGame() {
        return "test_game";
    }

    @GetMapping("/test_cd")
    public String testCd() {
        return "test_cd";
    }

    @GetMapping("/test_sb")
    public String testSb() {
        return "test_sb";
    }

    @RolesAllowed({"USER"})
    @GetMapping("/create_game")
    public String testCg() {
        return "create_game";
    }
}
