package com.example.controller;

import java.security.Principal;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class HelloController {
	ModelAndView modelAndView = new ModelAndView();

	@GetMapping("/")
	public ModelAndView index(HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();

		if(principal == null) {
			modelAndView.setViewName("index.html");
			return modelAndView;
		} else {
			modelAndView.setViewName("home.html");
			return modelAndView;
		}
	}
}
