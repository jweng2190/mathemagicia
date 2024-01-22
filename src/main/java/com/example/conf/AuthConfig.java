package com.example.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;

@Configuration
@ComponentScan
public class AuthConfig {
    @Bean
	public AuthenticationTrustResolver trustResolver() {
		return new AuthenticationTrustResolver() {

			@Override
			public boolean isRememberMe(final Authentication authentication) {
				return false;
			}

			@Override
			public boolean isAnonymous(final Authentication authentication) {
				return false;
			}
		};
	}
}
