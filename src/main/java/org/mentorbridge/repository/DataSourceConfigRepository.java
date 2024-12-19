package org.mentorbridge.repository;

import org.mentorbridge.entity.DataSourceConfigEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DataSourceConfigRepository extends MongoRepository<DataSourceConfigEntity, String> {
    // You can query the database based on the tenant ID or other identifiers
}