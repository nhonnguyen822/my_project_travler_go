package com.example.tourtravelserver.dto;

import com.example.tourtravelserver.enums.ServiceType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDTO {
    private Long id;
    private String name;
    private ServiceType type;
}
