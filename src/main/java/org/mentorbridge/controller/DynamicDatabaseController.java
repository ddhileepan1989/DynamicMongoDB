package org.mentorbridge.controller;

import lombok.RequiredArgsConstructor;
import org.mentorbridge.dto.DatabaseDTO;
import org.mentorbridge.service.DynamicDatabaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DynamicDatabaseController {


    private final DynamicDatabaseService dynamicDatabaseService;

    @PostMapping("/signUp")
    public String createDatabase(@RequestBody DatabaseDTO databaseDTO) {
        System.out.println("DatabaseDTO : " + databaseDTO.toString());
        dynamicDatabaseService.createDatabaseWithTemplate(
                databaseDTO.getDbName(),
                databaseDTO.getEmail());
        return "Created";
    }

    @GetMapping("/login/{email}")
    public List<String> login(@PathVariable String email) {
        System.out.println("email : " + email);
        return dynamicDatabaseService.readMongoTemplate(email);
    }
}
