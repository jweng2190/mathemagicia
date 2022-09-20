package com.example.dao;

import com.example.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query(value = "SELECT * FROM role WHERE role_name=?1", nativeQuery = true)
    Role getRoleByName(String roleName);
}
