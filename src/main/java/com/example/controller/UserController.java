package com.example.controller;

import com.example.dao.UserRepository;
import com.example.model.Game;
import com.example.model.User;
import com.example.service.EmailService;
import com.example.service.XpLevelService;

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
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserRepository userDao;

    @Autowired
    private EmailService es;

    @Autowired
    private XpLevelService xpLevelService;

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

    @GetMapping("/level")
    @RolesAllowed({"USER", "ADMIN"})
    public int currentUserLevel(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        String username = principal.getName();
        User currentUser = userDao.getUserByUsername(username);
        int level = currentUser.getLevel();
        return level;
    }

    @GetMapping("/xp")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<List<Integer>> currentUserXp(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        String username = principal.getName();
        User currentUser = userDao.getUserByUsername(username);
        int xp = currentUser.getXp();
        int level = currentUser.getLevel();
        int xpLevelUp = xpLevelService.getXpToLevelUp(level);
        return ResponseEntity.ok(Arrays.asList(xp, xpLevelUp));
    }

    @GetMapping("/all_games")
    @RolesAllowed({"USER"})
    public ResponseEntity<List<Game>> getAllGames(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        String username = principal.getName();
        User user = userDao.getUserByUsername(username);
        List<Game> allGames = user.getGames();
        return ResponseEntity.ok().body(allGames);
    }


    @PostMapping(path = "/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({"USER"})
    public ResponseEntity<ArrayList<List<String>>> searchUser(@RequestBody Map<String, String> payload) {
        String type = payload.get("searchType");
        if(type == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String searchValue = payload.get("searchValue");
        List<String> emailList = new ArrayList<>();
        if(type.equals("Default")) {
            String fName = searchValue.split(" ")[0].toLowerCase();
            String lName = searchValue.split(" ")[1].toLowerCase();

            List<String> fullNames = userDao.getAllFullNames();
            for(String fullName: fullNames) {
                if(fullName.contains(fName) || fullName.contains(lName)) {
                    List<String> emails = userDao.getEmailsByFullName(fullName);
                    for(String email: emails) {
                        if(!emailList.contains(email)) {
                            emailList.add(email);
                        }
                    }
                }
            }
        } else if(type.equals("Username")) {
            List<String> usernames = userDao.getAllUsernames();
            for(String username: usernames) {
                if(username.contains(searchValue)) {
                    String email = userDao.getEmailByUsername(username);
                    if(!emailList.contains(email)) {
                        emailList.add(email);
                    }
                }
            }
        } else if(type.equals("Level")) {
            //TODO
        }

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
