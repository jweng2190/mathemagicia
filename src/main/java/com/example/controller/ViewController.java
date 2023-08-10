package com.example.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.annotation.security.RolesAllowed;
import javax.websocket.server.PathParam;

@Controller
public class ViewController {
    @RolesAllowed({"USER"})
    @GetMapping("/view")
    public String renderProblems(@RequestParam(name="contest") String contest,
    @RequestParam(name="problemId") Integer problemId) {
        return "problem_template";
    }

    @RolesAllowed({"USER"})
    @GetMapping("/game/{gameId}")
    public String game(@PathVariable String gameId) {
        return "game_template";
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

    @RolesAllowed({"USER"})
    @GetMapping("/create_game")
    public String testCg() {
        return "create_game";
    }
}
