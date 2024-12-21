package org.mentorbridge.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mentorbridge.dto.RequestDTO;
import org.mentorbridge.dto.ResponseDTO;
import org.mentorbridge.entity.OrganizationEntity;
import org.mentorbridge.service.DynamicDatabaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final DynamicDatabaseService dynamicDatabaseService;

    /**
     * Creates a new database and performs user initialization with SignUp.
     *
     * @param requestDTO Contains the user details for database creation.
     * @return A response indicating the success or failure of the operation.
     */
    @PostMapping("/signUp")
    public ResponseEntity<Object> signUp(@RequestBody RequestDTO requestDTO) {
        log.info("Received request to create database: {}", requestDTO);

        try {
            // Input validation (basic validation)
            if (requestDTO.getDbName() == null || requestDTO.getDbName().isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Database name cannot be empty"));
            }
            if (requestDTO.getEmail() == null || requestDTO.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body(new ResponseDTO("Email cannot be empty"));
            }

            // Proceed with database creation and user initialization
            dynamicDatabaseService.createDatabaseWithTemplate(
                    requestDTO.getDbName(),
                    requestDTO.getEmail());

            // Log the successful creation
            log.info("SignUp completed successfully for email: {}", requestDTO.getEmail());

            // Return a success response
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO("SignUp completed successfully"));

        } catch (Exception e) {
            log.error("Error during database creation for email: {}", requestDTO.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("Failed to create the database due to an internal error"));
        }
    }

    /**
     * Handles login by email and returns the organization details.
     *
     * @param email The email used for logging in.
     * @return The OrganizationEntity associated with the email, or 404 if not found.
     */
    @GetMapping("/login/{email}")
    public ResponseEntity<Object> login(@PathVariable String email) {
        log.info("Login attempt for email: {}", email);

        // Fetch the organization entity wrapped in Optional
        Optional<OrganizationEntity> organizationOptional = dynamicDatabaseService.findDocumentsByEmailInDatabase(email);

        // If organization is found, return 200 OK
        if (organizationOptional.isPresent()) {
            return ResponseEntity.ok(organizationOptional.get());
        } else {
            // If not found, return 404 Not Found
            log.warn("Organization not found for email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO("Organization not found for email: " + email));
        }
    }
}