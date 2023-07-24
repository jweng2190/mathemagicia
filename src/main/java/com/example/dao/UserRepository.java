package com.example.dao;

import com.example.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.username = :username")
    User getUserByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    public User getUserByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.resetPasswordToken = :token")
    public User getUserByToken(@Param("token") String token);

    @Query(value="SELECT email FROM user WHERE CONCAT(first_name, ' ', last_name) = ?1", nativeQuery = true)
    List<String> getEmailsByFullName(String fullName);

    @Query(value="SELECT email FROM user WHERE username = ?1", nativeQuery = true)
    String getEmailByUsername(String username);

    @Query(value="SELECT username FROM user WHERE email = ?1", nativeQuery = true)
    String getUsernameByEmail(String email);

    @Query(value="SELECT first_name FROM user WHERE email = ?1", nativeQuery = true)
    String getFirstNameByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
    public User findByVerificationCode(String code);

    @Query(value="SELECT score FROM user WHERE username = ?1", nativeQuery = true)
    int getScoreByUsername(String username);

    @Transactional
    @Modifying
    //@Query(value="UPDATE user SET score=?1 WHERE username=?2", nativeQuery = true)
    @Query("UPDATE User u SET u.score = :score WHERE u.username = :username")
    void setScoreByUsername(int score, String username);
}
