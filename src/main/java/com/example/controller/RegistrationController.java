package com.example.controller;

import com.example.dao.UserRepository;
import com.example.model.User;
import com.example.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@RestController
public class RegistrationController {
    @Autowired
    private UserRepository userDao;

    @Autowired
    private UserService service;

    ModelAndView modelAndView = new ModelAndView();

    @GetMapping("/register")
    public ModelAndView showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        modelAndView.setViewName("registration_form.html");
        return modelAndView;
    }

    @PostMapping("/process_register")
    public ModelAndView processRegister(User user, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
        service.register(user, getSiteUrl(request));

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register_success.html");
        return modelAndView;
    }

    private String getSiteUrl(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @GetMapping("/verify")
    public ModelAndView verifyUser(@Param("code") String code) {
        if (service.verify(code)) {
            modelAndView.setViewName("verify_success.html");
        } else {
            modelAndView.setViewName("verify_fail.html");
        }
        return modelAndView;
    }
}
