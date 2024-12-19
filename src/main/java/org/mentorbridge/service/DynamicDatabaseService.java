package org.mentorbridge.service;

import com.mongodb.client.*;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.mentorbridge.entity.DataSourceConfigEntity;
import org.mentorbridge.entity.Organization;
import org.mentorbridge.repository.DataSourceConfigRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DynamicDatabaseService {


    private final DataSourceConfigRepository dataSourceConfigRepository;


    public MongoTemplate createMongoTemplate(String connectionString, String dbName) {
        MongoClient mongoClient = MongoClients.create(connectionString);
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient, dbName));
    }

    public void createDatabaseWithTemplate(String dbName, String email) {
        String connectionString = "mongodb+srv://dhilprojects:Q19R8CduAFVD9JmO@cluster0.051e3.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
        MongoTemplate mongoTemplate = createMongoTemplate(connectionString, dbName);

        MongoDatabase database = mongoTemplate.getDb();
        if (!database.listCollectionNames().into(new java.util.ArrayList<>()).contains("organization")) {
            database.createCollection("organization");
            System.out.println("Collection created: " + "organization");
        }

        // Access the collection
        MongoCollection<Document> collection = database.getCollection("organization");

        // Create an entity object
        Organization organization = Organization.builder().id(email).url(connectionString).driverClassName("com.mongodb.jdbc.MongoDriver").username("dhilprojects").password("Q19R8CduAFVD9JmO").dbName(dbName).build();

        // Convert the entity object to a BSON Document
        Document userDocument = organization.toDocument();

        // Insert the document into the collection
        collection.insertOne(userDocument);
        System.out.println("Organization inserted: " + userDocument.toJson());

        dataSourceConfigRepository.save(DataSourceConfigEntity.builder().id(email).url(connectionString).driverClassName("com.mongodb.jdbc.MongoDriver").username("dhilprojects").password("Q19R8CduAFVD9JmO").dbName(dbName).build());
    }

    public List<String> readMongoTemplate(String email) {

        // Fetch the DataSource configuration from MongoDB
        DataSourceConfigEntity dataSourceConfigEntity = dataSourceConfigRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("DataSource configuration not found"));

        String connectionString = "mongodb+srv://dhilprojects:Q19R8CduAFVD9JmO@cluster0.051e3.mongodb.net/" + dataSourceConfigEntity.getDbName() + "?retryWrites=true&w=majority&appName=Cluster0";

        List<String> organizations = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            // Access the database
            MongoDatabase database = mongoClient.getDatabase(dataSourceConfigEntity.getDbName());

            // Access the collection
            MongoCollection<Document> collection = database.getCollection("organization");

            // Read all documents in the collection
            FindIterable<Document> documents = collection.find();

            // Print the documents
            for (Document doc : documents) {
                System.out.println("Organization Table Records" + doc.toJson());
                organizations.add(doc.toJson());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return organizations;
    }
}
