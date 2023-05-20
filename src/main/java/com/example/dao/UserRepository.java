package com.example.dao;

import com.example.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.username = :username")
    User getUserByUsername(@Param("username") String username);

    @Query(value="SELECT email FROM user WHERE CONCAT(first_name, ' ', last_name) = ?1", nativeQuery = true)
    List<String> getEmailsByFullName(String fullName);

    @Query(value="SELECT email FROM user WHERE username = ?1", nativeQuery = true)
    String getEmailByUsername(String username);

    @Query(value="SELECT first_name FROM user WHERE email = ?1", nativeQuery = true)
    String getFirstNameByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
    public User findByVerificationCode(String code);
}
