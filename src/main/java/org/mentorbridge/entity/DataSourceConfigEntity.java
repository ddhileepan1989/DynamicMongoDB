package org.mentorbridge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "datasource_config")
@Builder
@AllArgsConstructor
@Data
public class DataSourceConfigEntity {

    @Id
    private String id;
    private String dbConnectionString;
    private String dbName;

    public DataSourceConfigEntity() {
    }
}
