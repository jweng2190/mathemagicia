package com.example.controller;

import com.example.dao.UserRepository;
import com.example.dto.VerificationRequest;
import com.example.model.User;
import com.example.service.UserService;
import com.example.service.UserService.USER_FIELD_FLAG;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userDao;

    @Autowired
    private UserService service;

    private static Logger log = LogManager.getLogger(RegistrationController.class);

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
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

    @GetMapping("/verify_success")
    public String verifySuccess() {
        return "verify_success";
    }

    @GetMapping("/verify_fail")
    public String verifyFail() {
        return "verify_fail";
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam("code") String code) {
        log.info("Verification Code: " + code);
        if (service.verify(code)) {
            return "verify_success";
        } else {
            return "verify_fail";
        }
    }
}
