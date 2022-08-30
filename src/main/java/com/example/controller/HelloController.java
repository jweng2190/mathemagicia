package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class HelloController {
	ModelAndView modelAndView = new ModelAndView();

	@GetMapping("/")
	public ModelAndView index() {
		modelAndView.setViewName("index.html");
		return modelAndView;
	}
}
