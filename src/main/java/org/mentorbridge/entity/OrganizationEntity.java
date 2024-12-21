package org.mentorbridge.entity;

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
}
