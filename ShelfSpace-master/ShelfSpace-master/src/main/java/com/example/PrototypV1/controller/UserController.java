package com.example.PrototypV1.controller;

import com.example.PrototypV1.model.*;
import com.example.PrototypV1.service.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public void register(@RequestBody User user) {
        userService.register(user);
    }

    @PostMapping("/login")
    public boolean login(@RequestBody User user) {
        return userService.login(user.getUsername(), user.getPassword());
    }
}
