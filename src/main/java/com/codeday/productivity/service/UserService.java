package com.codeday.productivity.service;

import com.codeday.productivity.entity.User;
import com.codeday.productivity.repository.UserRepository;
import com.codeday.productivity.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;
    private final IdGenerator idGenerator;

    @Autowired  // Constructor injection
    public UserService(UserRepository repository, IdGenerator idGenerator) {

        this.idGenerator = idGenerator;
        this.repository = repository;
    }
    public User saveUser(User user){
        int id = idGenerator.generateId();
        user.setId(id);
        return repository.save(user);
    }
    public List<User> saveUsers(List<User> users){
        return repository.saveAll(users);
    }

    public List<User> getUsers(){
        return repository.findAll();
    }

    public User getUserById(int id){
        return repository.findById(id).orElse(null);
    }

    public User getUserByFirstAndLastName(String firstName, String lastName){
        return repository.findByFirstNameAndLastName(firstName, lastName);
    }

    public User updateUser(User user){
        User existingUser = repository.findById(user.getId()).orElse(null);
        if (existingUser == null) {
            // Handle this case, maybe throw an exception or return null
            throw new RuntimeException("User not found with ID: " + user.getId());
        }
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        return repository.save(existingUser);
    }

    public String deactivateUser(int id) {
        User user = repository.findById(id).orElse(null);
        if (user != null) {
            user.setIsActive("N");
            repository.save(user);
            return "User deactivated || " + id;
        } else {
            return "User not found || " + id;
        }
    }

}

