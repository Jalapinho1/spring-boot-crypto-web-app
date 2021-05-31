package com.app.web.crypto.api.service;

import com.app.web.crypto.api.model.User;
import com.app.web.crypto.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByName(String name) {
        Optional<User> optionalUser = userRepository.findByUsername(name);
        User user = null;

        if(optionalUser.isPresent()) {
            user = optionalUser.get();
        }

        return user;
    }

}
