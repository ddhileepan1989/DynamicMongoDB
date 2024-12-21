package org.mentorbridge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mentorbridge.config.DynamicMongoConfig;
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
     *
     * @param organizationName The name of the organization.
     * @param email            The email (used as the unique ID).
     */
    public void createDatabaseWithTemplate(String organizationName, String email) {
        String dbNameFormatted = DBUtility.dbNameFormatter(organizationName);

        try {
            // Create a MongoTemplate for the dynamic database
            MongoTemplate mongoTemplate = dynamicMongoConfig.createMongoTemplate(dbNameFormatted);

            // Create an organization entity
            OrganizationEntity organizationEntity = OrganizationEntity.builder()
                    .id(email)  // Assuming 'id' is the email
                    .organizationName(organizationName)
                    .build();

            // Save the organization entity to the database
            mongoTemplate.save(organizationEntity);
            log.info("Organization entity saved for email: {}", email);

            // Save the data source configuration for future reference
            DataSourceConfigEntity dataSourceConfigEntity = DataSourceConfigEntity.builder()
                    .id(email)
                    .dbConnectionString(primaryDBConnectionString)
                    .dbName(dbNameFormatted)
                    .build();

            dataSourceConfigRepository.save(dataSourceConfigEntity);
            log.info("DataSourceConfigEntity saved for email: {}", email);
        } catch (Exception e) {
            log.error("Error creating database or inserting organization data for email: {}", email, e);
            throw new RuntimeException("Failed to create database or insert organization data", e);
        }
    }

    /**
     * Finds OrganizationEntity documents by email, querying a dynamic database based on the email.
     *
     * @param email The email (assumed to be the id) to query.
     * @return An OrganizationEntity document, or null if not found.
     */
    public Optional<OrganizationEntity> findDocumentsByEmailInDatabase(String email) {
        // Fetch the DataSource configuration from MongoDB
        DataSourceConfigEntity dataSourceConfigEntity = dataSourceConfigRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("DataSource configuration not found for email: " + email));

        // Create MongoTemplate for the given dynamic database
        MongoTemplate mongoTemplate = dynamicMongoConfig.createMongoTemplate(dataSourceConfigEntity.getDbName());

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
