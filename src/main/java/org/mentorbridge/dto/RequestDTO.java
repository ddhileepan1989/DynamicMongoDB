package org.mentorbridge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RequestDTO {
    private String dbName;
    private String email;
}
