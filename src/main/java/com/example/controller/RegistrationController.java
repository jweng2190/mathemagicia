package com.example.controller;

import com.example.dao.UserRepository;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;

@RestController
public class RegistrationController {
    @Autowired
    private UserRepository userDao;

    @GetMapping("/register")
    @RolesAllowed({"USER"})
    public ModelAndView showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("registration_form.html");
        return modelAndView;
    }

    @PostMapping("/process_register")
    public ModelAndView processRegister(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userDao.save(user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register_success.html");
        return modelAndView;
    }
}
