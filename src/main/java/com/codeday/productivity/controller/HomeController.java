package com.codeday.productivity.controller;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/test", produces = MediaType.TEXT_PLAIN_VALUE)
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to CodeDay Productivity Application!";
    }
}


