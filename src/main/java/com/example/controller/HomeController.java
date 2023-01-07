package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;

@RestController
public class HomeController {
    ModelAndView modelAndView = new ModelAndView();

    @RolesAllowed({"USER"})
    @GetMapping("/home")
    public ModelAndView home() {
        modelAndView.setViewName("home.html");
        return modelAndView;
    }

    @RolesAllowed({"USER"})
    @GetMapping("/game")
    public ModelAndView game() {
        modelAndView.setViewName("game.html");
        return modelAndView;
    }
}
