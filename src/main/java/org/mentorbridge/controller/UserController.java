package org.mentorbridge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
            summary = "Sign up a new user and create a database",
            description = "This API endpoint allows the creation of a new database for the organization and performs user initialization with the provided details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "SignUp completed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data (email or dbName is missing)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)))
    })
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
            dynamicDatabaseService.createDatabaseByOrganization(
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
    @Operation(
            summary = "Login using email and retrieve organization details",
            description = "This API endpoint allows users to login by email and fetch the associated organization details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found organization", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationEntity.class))),
            @ApiResponse(responseCode = "404", description = "Organization not found for the provided email", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class)))
    })
    public ResponseEntity<Object> login(@PathVariable String email) {
        log.info("Login attempt for email: {}", email);

        //Find the DB name by email
        String dbName = dynamicDatabaseService.findDBNameByEmailFromSourceDirectory(email);

        // Fetch the organization entity wrapped in Optional
        Optional<OrganizationEntity> organizationOptional = dynamicDatabaseService.findOrganizationByEmail(dbName, email);

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