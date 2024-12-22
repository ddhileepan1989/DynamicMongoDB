package org.mentorbridge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mentorbridge.config.DynamicMongoConfig;
import org.mentorbridge.custom.DatabaseNotFoundException;
import org.mentorbridge.entity.DataSourceConfigEntity;
import org.mentorbridge.entity.OrganizationEntity;
import org.mentorbridge.repository.DataSourceConfigRepository;
import org.mentorbridge.utilities.DBUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicDatabaseService {

    private final DynamicMongoConfig dynamicMongoConfig;

    @Value("${datasource.primary.connection.uri}")
    private String primaryDBConnectionString;

    private final DataSourceConfigRepository dataSourceConfigRepository;


    /**
     * Creates a new database and inserts the organization details.
     * This method first formats the database name based on the organization name, then creates a
     * dynamic MongoDB template for that database. Afterward, it creates an `OrganizationEntity`
     * with the provided email and organization name, and inserts it into the database. Finally,
     * the method saves the data source configuration (such as the database name and connection string)
     * for future reference.
     *
     * @param organizationName The name of the organization, which is used to generate the database name.
     * @param email The email address (used as a unique ID) of the organization.
     * @throws RuntimeException If there is an error creating the database or inserting the organization data.
     */
    public void createDatabaseByOrganization(String organizationName, String email) {
        // Format the database name based on the organization name
        String dbNameFormatted = DBUtility.dbNameFormatter(organizationName);

        try {
            // Create a MongoTemplate for the dynamic database, using the formatted database name
            MongoTemplate mongoTemplate = dynamicMongoConfig.createMongoTemplate(dbNameFormatted);

            // Create an organization entity using the provided email and organization name
            OrganizationEntity organizationEntity = OrganizationEntity.builder()
                    .id(email)
                    .organizationName(organizationName)
                    .build();

            // Save the organization entity to the dynamic database
            mongoTemplate.save(organizationEntity);
            log.info("Organization entity saved for email: {}", email);

            // Create a DataSourceConfigEntity to store the database connection information
            DataSourceConfigEntity dataSourceConfigEntity = DataSourceConfigEntity.builder()
                    .id(email)
                    .dbConnectionString(primaryDBConnectionString)  // The connection string for the primary database
                    .dbName(dbNameFormatted)  // The formatted database name
                    .build();

            // Save the data source configuration to the repository
            dataSourceConfigRepository.save(dataSourceConfigEntity);
            log.info("DataSourceConfigEntity saved for email: {}", email);

        } catch (Exception e) {
            // Log and rethrow the exception if any error occurs during the database creation or data insertion
            log.error("Error creating database or inserting organization data for email: {}", email, e);
            throw new RuntimeException("Failed to create database or insert organization data", e);
        }
    }



    /**
     * Finds the database name associated with the given email from the data source configuration.
     * <p>
     * This method looks up the DataSourceConfigEntity in the repository using the provided email
     * and extracts the associated database name. If no configuration is found for the email,
     * a custom exception is thrown after logging the error.
     *
     * @param email The email used to retrieve the database configuration.
     * @return The database name associated with the given email.
     * @throws DatabaseNotFoundException if no DataSource configuration is found for the provided email.
     */
    public String findDBNameByEmailFromSourceDirectory(String email) {
        // Attempt to fetch the DataSource configuration from the repository using the email
        return dataSourceConfigRepository.findById(email)
                .map(DataSourceConfigEntity::getDbName)  // If found, map to the dbName field
                .orElseThrow(() -> {  // If not found, throw a custom exception after logging the error
                    log.error("Database configuration not found for email: {}", email);  // Log the error for debugging
                    return new DatabaseNotFoundException("Database configuration not found for email: " + email);  // Throw a custom exception
                });
    }


    /**
     * Finds OrganizationEntity documents by email, querying a dynamic database based on the email.
     *
     * @param email The email (assumed to be the id) to query.
     * @return An OrganizationEntity document, or null if not found.
     */
    public Optional<OrganizationEntity> findOrganizationByEmail(String dbName,String email) {
        // Create MongoTemplate for the given dynamic database
        MongoTemplate mongoTemplate = dynamicMongoConfig.createMongoTemplate(dbName);

        // Create the query to search for an organization by email
        Query query = new Query(Criteria.where("id").is(email));

        try {
            // Use MongoTemplate to query data and return the single matching document
            OrganizationEntity organization = mongoTemplate.findOne(query, OrganizationEntity.class);

            if (organization == null) {
                log.warn("No organization found for email: {}", email);
                return Optional.empty(); // Return empty Optional if not found
            } else {
                log.info("Found organization for email: {}", email);
                return Optional.of(organization); // Return wrapped organization
            }
        } catch (Exception e) {
            log.error("Error occurred while querying for organization with email: {}", email, e);
            throw new RuntimeException("Failed to query organization for email: " + email, e);
        }
    }
}
