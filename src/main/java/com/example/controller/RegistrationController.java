package com.example.controller;

import com.example.dao.UserRepository;
import com.example.model.User;
import com.example.service.UserService;
import com.example.service.UserService.USER_FIELD_FLAG;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RegistrationController {
    @Autowired
    private UserRepository userDao;

    @Autowired
    private UserService service;

    ModelAndView modelAndView = new ModelAndView();

    @GetMapping("/register")
    public ModelAndView showRegistrationForm(Model model) {
        modelAndView.setViewName("register.html");
        return modelAndView;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> processRegister(@RequestBody MultiValueMap<String, String> formData, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
        USER_FIELD_FLAG check = service.register(formData, getSiteUrl(request));

        if(check == USER_FIELD_FLAG.SUCCESS) {
            return ResponseEntity.ok("Success");
        } else if(check == USER_FIELD_FLAG.USERNAME_EXISTING){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("username existing");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email existing");
        }
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
