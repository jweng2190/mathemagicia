package com.example.service;

import java.security.Principal;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.dao.UserRepository;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository repo;

    @Autowired
    private GameService gameService;

    public void sendInviteEmail(String recipientEmail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(new InternetAddress("teammathemagicia@gmail.com", false));
        message.setRecipients(MimeMessage.RecipientType.TO, recipientEmail);
        message.setSubject("Mathemagicia 1v1 Game Invitation");

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String senderUsername = null;
        if (principal instanceof UserDetails) {
            senderUsername = ((UserDetails)principal).getUsername();
        } else {
            senderUsername = principal.toString();
        }

        String recipientName = repo.getFirstNameByEmail(recipientEmail);

        String gameCode = gameService.getGameCodeByUsername(senderUsername);
        String gameUrl = "http://localhost:8080/game/" + gameCode;

        String htmlContent = "Hi [[name]],<br>"
        + "[[senderUsername]] is inviting you to play a 1v1 game on Mathemagicia.<br>"
        + "Please click the link below to accept the invite:<br>"
        + "<h3><a href=\"[[URL]]\" target=\"_blank\">JOIN GAME</a></h3>"
        + "Thank you,<br>"
        + "The Mathemagicia Team";

        htmlContent = htmlContent.replace("[[name]]", recipientName);
        htmlContent = htmlContent.replace("[[senderUsername]]", senderUsername);
        htmlContent = htmlContent.replace("[[URL]]", gameUrl);

        message.setContent(htmlContent, "text/html; charset=utf-8");
        mailSender.send(message);
    }
}
