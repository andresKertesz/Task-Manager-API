package com.akertesz.task_manager_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akertesz.task_manager_api.model.User;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByUsernameAndPassword(String username, String password);
    User findByEmailAndPassword(String email, String password); 
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndPassword(String username, String password);
    boolean existsByEmailAndPassword(String email, String password);
}
