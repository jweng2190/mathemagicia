package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {
    @RequestMapping("/view")
    public String renderProblems(@RequestParam(name="contest") String contest) {
        return "problem_template";
    }
}
