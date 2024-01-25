package com.example.controller;

import com.example.dao.UserRepository;
import com.example.model.Game;
import com.example.model.User;
import com.example.service.EmailService;
import com.example.service.UserService;
import com.example.service.XpLevelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private UserService userService;
    
    public static final int MAX_GAMES = 50;

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
    public int currentUserLevel(@RequestParam("username") String username) {
        return userService.getLevel(username);
    }

    @GetMapping("/user_level")
    @RolesAllowed({"USER", "ADMIN"})
    public int currentUserLevel(HttpServletRequest httpServletRequest) {
        Principal principal = httpServletRequest.getUserPrincipal();
        String username = principal.getName();
        return userService.getLevel(username);
    }

    @GetMapping("/xp")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<List<Integer>> currentUserXp(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        String username = principal.getName();
        User currentUser = userService.getUser(username);
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
        User user = userDao.findByUsername(username);
        List<Game> allGames = user.getGames();
        int numGames = allGames.size();

        if(numGames < MAX_GAMES) {
            return ResponseEntity.ok().body(allGames);
        } else {
            return ResponseEntity.ok().body(allGames.subList(0, MAX_GAMES));
        }
    }


    @PostMapping(path = "/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({"USER"})
    public ResponseEntity<ArrayList<List<String>>> searchUser(@RequestBody Map<String, String> payload, Principal principal) {
        String type = payload.get("searchType");
        if(type == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String searchValue = payload.get("searchValue");
        List<String> emailList = new ArrayList<>();
        if(type.equals("Default")) {
            String[] splittedName = searchValue.split("\\s+");

            List<String> fullNames = userDao.getAllFullNames();
            for(String fullName: fullNames) {
                for(String partName: splittedName) {
                    if(fullName.contains(partName.toLowerCase())) {
                        List<String> emails = userDao.getEmailsByFullName(fullName);
                        for(String email: emails) {
                            if(!emailList.contains(email)) {
                                emailList.add(email);
                            }
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
            List<User> users = userDao.findAll();
            int min = Integer.parseInt(searchValue.split(",")[0]);
            int max = Integer.parseInt(searchValue.split(",")[1]);

            for(User user: users) {
                if(user.getLevel() >= min && user.getLevel() <= max) {
                    String email = user.getEmail();
                    if(!emailList.contains(email)) {
                        emailList.add(email);
                    }
                }
            }
        }

        ArrayList<List<String>> usernameAndEmailList = new ArrayList<List<String>>();

        for(int i = 0; i < emailList.size(); i++) {
            String email = emailList.get(i);
            String username = userDao.getUsernameByEmail(email);
            //prevent user from inviting themselves
            if(!username.equals(principal.getName())) {
                usernameAndEmailList.add(Arrays.asList(username, email));
            }
        }

        if(!usernameAndEmailList.isEmpty()) {
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

    @GetMapping("/profile_data")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<List<Integer>> userProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String username = auth.getName();
        List<Integer> profileData = userService.getProfileData(username);
        return ResponseEntity.ok().body(profileData);
    }
}
