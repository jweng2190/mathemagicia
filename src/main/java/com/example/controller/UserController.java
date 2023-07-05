package com.example.controller;

import com.example.dao.UserRepository;
import com.example.model.User;
import com.example.service.EmailService;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserRepository userDao;

    @Autowired
    private EmailService es;

    @GetMapping("/users")
    @RolesAllowed({"ADMIN"})
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userDao.findAll();
        return ResponseEntity.ok().body(allUsers);
    }

    @GetMapping("/username")
    @RolesAllowed({"USER"})
    public String currentUserName(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        return principal.getName();
    }

    @PostMapping(path = "/search", consumes = MediaType.TEXT_PLAIN_VALUE)
    @RolesAllowed({"USER"})
    public ResponseEntity<ArrayList<List<String>>> searchUser(@RequestBody String fullName) {
        List<String> emailList = userDao.getEmailsByFullName(fullName);
        ArrayList<List<String>> usernameAndEmailList = new ArrayList<List<String>>();

        for(int i = 0; i < emailList.size(); i++) {
            String email = emailList.get(i);
            String username = userDao.getUsernameByEmail(email);

            usernameAndEmailList.add(i, Arrays.asList(username, email));
        }

        if(!emailList.isEmpty()) {
            return ResponseEntity.ok().body(usernameAndEmailList);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping(path = "/email", consumes = MediaType.TEXT_PLAIN_VALUE)
    @RolesAllowed({"USER"})
    public ResponseEntity<List<String>> emailUser(@RequestBody String email) throws MessagingException {
        es.sendInviteEmail(email);
        String msg = "Email sent successfully";
        List<String> responseList = Arrays.asList(new String[] {msg});
        return ResponseEntity.ok().body(responseList);
    }
}
