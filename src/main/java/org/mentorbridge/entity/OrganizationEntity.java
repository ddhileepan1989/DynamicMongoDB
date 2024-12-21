package org.mentorbridge.entity;

import org.bson.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;


@Builder
@AllArgsConstructor
@Data
public class OrganizationEntity {
    @Id
    private String id;
    private String organizationName;

    // Convert to BSON Document
    public Document toDocument() {
        return new Document("id", id)
                .append("organizationName", organizationName);
    }

    // Create from BSON Document
    public static OrganizationEntity fromDocument(Document document) {
        return new OrganizationEntity(
                document.getString("id"),
                document.getString("organizationName")
        );
    }
}
