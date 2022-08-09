package com.example.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().passwordEncoder(new BCryptPasswordEncoder()).dataSource(dataSource)
                .usersByUsernameQuery("select username, password, enabled from user where username=?")
                .authoritiesByUsernameQuery("SELECT u.username, r.role_name AS role FROM user AS u JOIN user_role AS ur ON u.user_id=ur.user_id " +
                        "JOIN role AS r ON r.role_id=ur.role_id WHERE u.username=?");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/signup", "/login")
                .permitAll().anyRequest().authenticated().and().formLogin()
                .and().httpBasic();
    }
}
