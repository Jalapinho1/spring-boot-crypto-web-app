package com.app.web.crypto.api.controller;

import com.app.web.crypto.api.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(value = "/api")
public class KeyController {

    @Autowired
    KeyService keyService;

    @GetMapping("/generatekeys")
    public Map<String, String> generateKeys() throws Exception {
        return keyService.generateKeys();
    }

    @GetMapping("/getkeys")
    public Map<String, String> getKeys() throws Exception {
        return keyService.getKeys();
    }

}
