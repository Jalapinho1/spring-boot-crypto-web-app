package com.app.web.crypto.api.controller;


import com.app.web.crypto.api.security.CurrentUser;
import com.app.web.crypto.api.security.UserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/users")
public class UserController {

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public String getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        String userSummary =  currentUser.getUsername();
        return userSummary;
    }

    @GetMapping("/hello")
    public String getmessage() {
        return "Hello User";
    }

}