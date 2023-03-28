package com.example.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.annotation.security.RolesAllowed;

@Controller
public class ViewController {
    @RolesAllowed({"USER"})
    @GetMapping("/view")
    public String renderProblems(@RequestParam(name="contest") String contest,
    @RequestParam(name="problemId") Integer problemId) {
        return "problem_template";
    }

    @RolesAllowed({"USER"})
    @GetMapping("/game")
    public String game() {
        return "game_template";
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
}
