package com.app.web.crypto.api.controller;

import com.app.web.crypto.api.model.Role;
import com.app.web.crypto.api.model.RoleName;
import com.app.web.crypto.api.model.User;
import com.app.web.crypto.api.payload.ApiResponse;
import com.app.web.crypto.api.payload.JwtAuthenticationResponse;
import com.app.web.crypto.api.payload.LoginRequest;
import com.app.web.crypto.api.payload.SignUpRequest;
import com.app.web.crypto.api.repository.RoleRepository;
import com.app.web.crypto.api.repository.UserRepository;
import com.app.web.crypto.api.security.CustomPasswordEncoder;
import com.app.web.crypto.api.security.JwtTokenProvider;
import com.app.web.crypto.api.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordService passwordService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, authorities.toString()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) throws Exception {

        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            ApiResponse apiResponse = new ApiResponse(false, "Username is already taken!");
            return new ResponseEntity(apiResponse, HttpStatus.BAD_REQUEST);
        }

        CustomPasswordEncoder passwordEncoder = new CustomPasswordEncoder();

        // Creating user's account ss
        User user = new User(signUpRequest.getUsername(), signUpRequest.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new Exception("User Role not set."));

        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(user.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

}
