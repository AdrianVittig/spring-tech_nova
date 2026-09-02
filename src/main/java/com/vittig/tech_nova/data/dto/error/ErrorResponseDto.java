package com.vittig.tech_nova.data.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {
    private LocalDateTime timeStamp;
    private String message;
    private String error;
    private String path;
    private int status;
    private Map<String, String> validationErrors;
}
