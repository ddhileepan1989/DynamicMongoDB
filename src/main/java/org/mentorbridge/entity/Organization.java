package org.mentorbridge.entity;

import org.bson.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Builder
@AllArgsConstructor
@Data
public class Organization {


    private String id;  // MongoDB _id, e.g., tenant1, tenant2
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private String dbName;

    // Convert to BSON Document
    public Document toDocument() {
        return new Document("id", id)
                .append("url", url)
                .append("username", username)
                .append("password", password)
                .append("driverClassName", driverClassName)
                .append("dbName", dbName);
    }

    // Create from BSON Document
    public static Organization fromDocument(Document document) {
        return new Organization(
                document.getString("id"),
                document.getString("url"),
                document.getString("username"),
                document.getString("password"),
                document.getString("driverClassName"),
                document.getString("dbName")
        );
    }
}
