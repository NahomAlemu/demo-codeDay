package com.codeday.productivity.repository;

import com.codeday.productivity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>{
    User findByFirstNameAndLastName(String firstName, String lastName);
}

