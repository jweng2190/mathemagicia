package com.example.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
	private AuthenticationTrustResolver trustResolver;
	public HelloController(AuthenticationTrustResolver trustResolver) {
        this.trustResolver = trustResolver;
    }

	@GetMapping("/")
	public String index() {
		Authentication authentication = getAuthentication();

        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "index";
        }

        return "redirect:/home";
	}

	/**
	 * Obtain the current active <code>Authentication</code>
	 *
	 * @return the authentication object or <code>null</code>
	 */
	private Authentication getAuthentication() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (!trustResolver.isAnonymous(auth)) {
			return auth;
		}

		return null;
	}

	
}
