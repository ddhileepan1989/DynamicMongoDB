package org.mentorbridge.service;

import com.mongodb.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.mentorbridge.entity.DataSourceConfigEntity;
import org.mentorbridge.entity.OrganizationEntity;
import org.mentorbridge.repository.DataSourceConfigRepository;
import org.mentorbridge.utilities.DBUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicDatabaseService {

    @Value("${datasource.primary.connection.url}")
    private String primaryDBConnectionString;

    private final DataSourceConfigRepository dataSourceConfigRepository;


    public MongoTemplate createMongoTemplate(String connectionString, String dbName) {
        MongoClient mongoClient = MongoClients.create(connectionString);
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient, dbName));
    }

    public void createDatabaseWithTemplate(String organizationName, String email) {
        String dbNameFormatted=DBUtility.dbNameFormatter(organizationName);
        MongoTemplate mongoTemplate = createMongoTemplate(primaryDBConnectionString, dbNameFormatted);

        MongoDatabase database = mongoTemplate.getDb();
        if (!database.listCollectionNames().into(new java.util.ArrayList<>()).contains("organization")) {
            database.createCollection("organization");
            log.info("Collection created: {}", "organization");
        }

        // Access the collection
        MongoCollection<Document> collection = database.getCollection("organization");

        // Create an entity object
        OrganizationEntity organizationEntity = OrganizationEntity.builder().id(email).organizationName(organizationName).build();

        // Convert the entity object to a BSON Document
        Document userDocument = organizationEntity.toDocument();

        // Insert the document into the collection
        collection.insertOne(userDocument);
        log.info("Organization inserted : {}", userDocument.toJson());

        dataSourceConfigRepository.save(DataSourceConfigEntity.builder().id(email).dbConnectionString(primaryDBConnectionString).dbName(dbNameFormatted).build());
    }

    public List<String> readMongoTemplate(String email) {

        // Fetch the DataSource configuration from MongoDB
        DataSourceConfigEntity dataSourceConfigEntity = dataSourceConfigRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("DataSource configuration not found"));

        List<String> organizations = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create(primaryDBConnectionString)) {
            // Access the database
            MongoDatabase database = mongoClient.getDatabase(dataSourceConfigEntity.getDbName());

            // Access the collection
            MongoCollection<Document> collection = database.getCollection("organization");

            // Read all documents in the collection
            FindIterable<Document> documents = collection.find();

            // Print the documents
            for (Document doc : documents) {
                log.info("Organization Table Records : {}", doc.toJson());
                organizations.add(doc.toJson());
            }
        } catch (Exception e) {
            log.error("error occurs : {}", Arrays.toString(e.getStackTrace()));
        }
        return organizations;
    }
}
