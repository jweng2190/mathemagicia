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
import com.example.model.GameStatus;
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
    public String game(@PathVariable String gameId) {
        Game game = gameDao.getGameByGameId(gameId);
        if(game == null) {
            return "game_error";
        }

        if(game.getStatus() != GameStatus.NEW) {
            return "game_error";
        }

        return "game_template";
    }

    @RolesAllowed({"USER"})
    @GetMapping("/review/{gameId}")
    public String reviewGame(Principal principal, @PathVariable String gameId) {
        return "review_template";
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

    @GetMapping("/past_games")
    public String pastGames() {
        return "past_games";
    }

    @RolesAllowed({"USER"})
    @GetMapping("/play_computer")
    public String playComputer() {
        return "computer";
    }

    @GetMapping("/quick_play")
    public String quickPlay() {
        return "quick_play";
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

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @RolesAllowed({"USER"})
    @GetMapping("/create_game")
    public String cg() {
        return "create_game";
    }

    @RolesAllowed({"USER"})
    @GetMapping("/profile")
    public String userProfile() {
        return "profile";
    }

    @GetMapping("/register_success")
    public String registerSuccess() {
        return "register_success";
    }

    @GetMapping("/register_fail")
    public String registerFail() {
        return "register_fail";
    }

    @GetMapping("/learn")
    public String learn() {
        return "learn";
    }

    @GetMapping("/learn/{learnType}")
    public String showLearnPage(@PathVariable("learnType") int type) {
        switch(type) {
            case 1:
                return "learn/fundamentals";
            case 2:
                return "learn/strategies";
            case 3:
                return "learn/advanced";
        }

        return "error/404.html";
    }

    @GetMapping("/fundamentals/{topic}")
    public String showFundamentalTopic(@PathVariable("topic") int type) {
        switch(type) {
            case 1:
                return "fundamentals/algebra";
            case 2:
                return "fundamentals/geometry";
            case 3:
                return "fundamentals/num_theory";
            case 4:
                return "fundamentals/probability";
        }

        return "error/404.html";
    }

    @GetMapping("/terms_of_use")
    public String showTermsOfUse() {
        return "terms";
    }
}
