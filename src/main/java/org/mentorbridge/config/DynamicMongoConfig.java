package org.mentorbridge.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class DynamicMongoConfig {

    @Value("${datasource.primary.connection.uri}")
    private String primaryConnectionUri;

    /**
     * This method will return a MongoTemplate connected to a dynamic database.
     * @param databaseName The name of the database to connect to dynamically.
     * @return MongoTemplate instance for the selected database.
     */
    public MongoTemplate createMongoTemplate(String databaseName) {
        MongoClient mongoClient = com.mongodb.client.MongoClients.create(primaryConnectionUri);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient, mongoDatabase.getName()));
    }
}
