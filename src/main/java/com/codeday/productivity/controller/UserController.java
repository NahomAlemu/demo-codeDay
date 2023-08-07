package com.codeday.productivity.controller;

import com.codeday.productivity.entity.User;
import com.codeday.productivity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/users")
    public User addUser(@RequestBody User user){
        return service.saveUser(user);
    }

    /**
     * I've added a /batch suffix to distinguish it from the single user creation.
     */
    @PostMapping("/users/batch")
    public List<User> addUsers(@RequestBody List<User> users){
        return service.saveUsers(users);
    }

    /**
     * When querying by name, you can use /users?firstName=John&lastName=Doe.
     */
    @GetMapping("/users")
    public List<User> findAllUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName
    ) {
        if (firstName != null && lastName != null) {
            return List.of(service.getUserByFirstAndLastName(firstName, lastName));
        }
        return service.getUsers();
    }

    @GetMapping("/users/{id}")
    public User findUserById(@PathVariable int id){
        return service.getUserById(id);
    }


    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User user) {
        user.setId(id);
        return service.updateUser(user);
    }

    @PutMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable int id) {
        return service.deactivateUser(id);
    }

}

