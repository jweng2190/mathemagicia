package com.example.controller;

import com.example.dao.UserRepository;
import com.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserRepository userDao;

    @GetMapping("/users")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userDao.findAll();
        return ResponseEntity.ok().body(allUsers);
    }
}
