package com.inflowia.medicflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(
    title = "Medicflow API", version = "v1",
    description = "API da Plataforma Médica Medicflow"
))
@SpringBootApplication
@ComponentScan(basePackages = "com.inflowia.medicflow")
public class MedicflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicflowApplication.class, args);
	}

}
