package org.mentorbridge.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mentorbridge.dto.UserRequestDTO;
import org.mentorbridge.service.DynamicDatabaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {


    private final DynamicDatabaseService dynamicDatabaseService;

    @PostMapping("/signUp")
    public String createDatabase(@RequestBody UserRequestDTO userRequestDTO) {
        log.info("userRequestDTO : {}", userRequestDTO.toString());
        dynamicDatabaseService.createDatabaseWithTemplate(
                userRequestDTO.getDbName(),
                userRequestDTO.getEmail());
        return "Created";
    }

    @GetMapping("/login/{email}")
    public List<String> login(@PathVariable String email) {
        log.info("email : {}", email);
        return dynamicDatabaseService.readMongoTemplate(email);
    }
}
