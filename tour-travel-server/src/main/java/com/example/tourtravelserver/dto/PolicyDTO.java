package com.example.tourtravelserver.dto;

import com.example.tourtravelserver.enums.PolicyType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyDTO {
    private Long id;
    private String name;
    private PolicyType type;
}
