package com.inflowia.medicflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inflowia.medicflow.config.CorrelationIdFilter;
import com.inflowia.medicflow.dto.medico.MedicoSelectDTO;
import com.inflowia.medicflow.security.ApiAccessDeniedHandler;
import com.inflowia.medicflow.security.ApiAuthenticationEntryPoint;
import com.inflowia.medicflow.security.JwtAuthenticationFilter;
import com.inflowia.medicflow.service.MedicoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MedicoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.inflowia.medicflow.exception.ControllerExceptionHandler.class)
@ActiveProfiles("test")
class MedicoControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MedicoService medicoService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CorrelationIdFilter correlationIdFilter;

    @MockBean
    private ApiAuthenticationEntryPoint authenticationEntryPoint;

    @MockBean
    private ApiAccessDeniedHandler accessDeniedHandler;

    @Test
    void shouldReturnSummaryForAutocomplete() throws Exception {
        when(medicoService.listarResumo("ana", 5)).thenReturn(List.of(
                new MedicoSelectDTO(1L, "Ana Silva", "CRM-1", "Cardiologia")
        ));

        mockMvc.perform(get("/medicos/autocomplete")
                        .param("termo", "ana")
                        .param("limite", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nomeCompleto").value("Ana Silva"))
                .andExpect(jsonPath("$[0].crm").value("CRM-1"));

        verify(medicoService).listarResumo("ana", 5);
    }
}
