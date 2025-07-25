package com.example.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/join")
    public String join() {
        return "join";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
