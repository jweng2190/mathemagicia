package com.example.service;

import com.example.dao.RoleRepository;
import com.example.dao.UserRepository;
import com.example.model.Role;
import com.example.model.User;
import com.exception.CustomerNotFoundException;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;

    @Autowired
    private RoleRepository roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    private void encodePassword(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
         
        user.setResetPasswordToken(null);
        repo.save(user);
    }

    public User getByResetPasswordToken(String token) {
        return repo.getUserByToken(token);
    }

    public void updateResetPasswordToken(String token, String email) throws CustomerNotFoundException {
        User user = repo.getUserByEmail(email);
        if (user != null) {
            user.setResetPasswordToken(token);
            repo.save(user);
        } else {
            throw new CustomerNotFoundException("Could not find any customer with the email " + email);
        }
    }

    public void sendResetEmail(String recipientEmail, String link)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("teammathemagicia@gmail.com", "Mathemagicia");
        helper.setTo(recipientEmail);

        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

    public boolean register(User user, String siteURL)
            throws UnsupportedEncodingException, MessagingException {
        
        String username = user.getUsername();
        String email = user.getEmail();
        //check for duplicate username or email
        if(repo.findByUsername(username) != null || repo.getUserByEmail(email) != null) {
            return false;
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        String randomCode = RandomString.make(64);
        user.setVerificationCode(randomCode);
        user.setEnabled(false);
        Set<Role> userRoles = new HashSet<>();
        Role role = roleDao.getRoleByName("ROLE_USER");
        userRoles.add(role);
        user.setRoles(userRoles);

        repo.save(user);

        sendVerificationEmail(user, siteURL);
        return true;
    }

    private void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "teammathemagicia@gmail.com";
        String senderName = "Mathemagicia";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                +"Thank you for registering an account at Mathemagicia.<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "The Mathemagicia Team";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFullName());
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

    public boolean verify(String verificationCode) {
        User user = repo.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            repo.save(user);

            return true;
        }

    }

    @Cacheable(value = "levelCache", key = "#username")
    public int getLevel(String username) {
        User user = repo.findByUsername(username);
        return user.getLevel();
    }

    @Cacheable(value = "usernameCache", key= "#username")
    public User getUser(String username) {
        User user = repo.findByUsername(username);
        return user;
    }

    //TODO
    /* @Cacheable(value = "searchCache", key= "#payload")
    public ArrayList<List<String>> getEmailListBySearch(Map<String, String> payload) {
        String type = payload.get("searchType");
        if(type == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String searchValue = payload.get("searchValue");
        List<String> emailList = new ArrayList<>();
        if(type.equals("Default")) {
            String[] splittedName = searchValue.split("\\s+");

            List<String> fullNames = repo.getAllFullNames();
            for(String fullName: fullNames) {
                for(String partName: splittedName) {
                    if(fullName.contains(partName.toLowerCase())) {
                        List<String> emails = repo.getEmailsByFullName(fullName);
                        for(String email: emails) {
                            if(!emailList.contains(email)) {
                                emailList.add(email);
                            }
                        }
                    }
                }
            }
        } else if(type.equals("Username")) {
            List<String> usernames = repo.getAllUsernames();
            for(String username: usernames) {
                if(username.contains(searchValue)) {
                    String email = repo.getEmailByUsername(username);
                    if(!emailList.contains(email)) {
                        emailList.add(email);
                    }
                }
            }
        } else if(type.equals("Level")) {
            //TODO
            List<User> users = repo.findAll();
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
            String username = repo.getUsernameByEmail(email);
            //prevent user from inviting themselves
            if(!username.equals(principal.getName())) {
                usernameAndEmailList.add(Arrays.asList(username, email));
            }
        }
    } */
}
