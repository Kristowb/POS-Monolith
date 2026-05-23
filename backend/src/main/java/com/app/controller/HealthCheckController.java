package com.app.controller;

import com.app.dto.HealthCheckDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<HealthCheckDto> checkHealth() {
        HealthCheckDto healthReport = HealthCheckDto.builder()
                .status("UP")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .version("1.0.0-SNAPSHOT")
                .build();
        return ResponseEntity.ok(healthReport);
    }
}
