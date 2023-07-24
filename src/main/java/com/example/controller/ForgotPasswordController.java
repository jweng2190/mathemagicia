package com.example.controller;
import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.Utility;
import com.example.model.User;
import com.example.service.UserService;
import com.exception.CustomerNotFoundException;

import net.bytebuddy.utility.RandomString;

@Controller
public class ForgotPasswordController {
@Autowired
    private JavaMailSender mailSender;
     
    @Autowired
    private UserService userService;
     
    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() {
        return "forgot_password_form";
    }
 
    @PostMapping("/forgot_password")
    public ResponseEntity<String> processForgotPassword(HttpServletRequest request) {
        String email = request.getParameter("email");
        String token = RandomString.make(30);

        try {
            userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            userService.sendResetEmail(email, resetPasswordLink);
            return ResponseEntity.ok().body("Password reset link sent to your email!");
        } catch (CustomerNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with provided email.");
        } catch (UnsupportedEncodingException | MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Please enter a valid email address.");
        }
    }
     
     
    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token) {
        User user = userService.getByResetPasswordToken(token);

        if (user == null) {
            return "invalid_token";
        }

        return "reset_password_form";
    }
     
    @PostMapping("/reset_password")
    public ResponseEntity<String> processResetPassword(HttpServletRequest request) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
         
        User user = userService.getByResetPasswordToken(token);
         
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Please try again later.");
        } else {           
            userService.updatePassword(user, password);
            return ResponseEntity.ok().body("You have successfully changed your password!");
        }
    }
}
