package com.inflowia.medicflow.security;

import io.jsonwebtoken.io.DecodingException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void shouldGenerateAndValidateTokenWithValidBase64Key() {
        JwtService jwtService = new JwtService("EbbYhazVAo07/7IzbLdTqLdMW2GfmJZeQGIEE+RrjGA=", 86_400_000L);
        UserDetails user = User.withUsername("doctor")
                .password("secret")
                .authorities("ROLE_MEDICO")
                .build();

        String token = jwtService.generateToken(user);

        assertEquals("doctor", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void shouldRejectShortJwtSecret() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> new JwtService("MTIzNDU2Nzg5MDEyMzQ1Njc4OTA=", 86_400_000L)
        );

        assertTrue(exception.getMessage().contains("256 bits"));
    }

    @Test
    void shouldRejectInvalidBase64JwtSecret() {
        assertThrows(
                DecodingException.class,
                () -> new JwtService("not-base64", 86_400_000L)
        );
    }
}