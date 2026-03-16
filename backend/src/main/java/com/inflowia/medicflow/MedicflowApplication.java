package com.inflowia.medicflow;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Medicflow API",
                version = "v1",
                description = "API da Plataforma Médica Medicflow"
        )
)
@SpringBootApplication
public class MedicflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicflowApplication.class, args);
    }
}