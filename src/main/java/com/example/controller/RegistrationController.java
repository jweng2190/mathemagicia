package com.example.controller;

import com.example.dao.UserRepository;
import com.example.model.User;
import com.example.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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

        modelAndView.setViewName("register.html");
        return modelAndView;
    }

    @PostMapping("/process_register")
    public ModelAndView processRegister(@Validated User user, BindingResult result, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
        service.register(user, getSiteUrl(request));
        /* int statusCode = (int) request.getAttribute("javax.servlet.error.status_code");
        System.out.println("Registration Status: " + statusCode);

        if(statusCode != 200) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("register_fail.html");
            return modelAndView;
        } */
        if(result.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("register_fail.html");
            return modelAndView;
        }
        userDao.save(user);

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
