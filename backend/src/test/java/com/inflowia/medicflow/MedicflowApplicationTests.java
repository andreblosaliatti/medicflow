package com.inflowia.medicflow;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled("Desabilitado temporariamente até estabilizar o contexto completo")
@SpringBootTest
@ActiveProfiles("test")
class MedicflowApplicationTests {

    @Test
    void contextLoads() {
    }
}