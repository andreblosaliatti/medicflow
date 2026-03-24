package com.inflowia.medicflow.domain;

import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.security.AccessRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainVocabularyTest {

    @Test
    void shouldNormalizeLegacyPaymentAliasesToCanonicalValues() {
        assertEquals(MeioPagamento.CREDITO, MeioPagamento.fromValue("CARTAO"));
        assertEquals(MeioPagamento.CREDITO, MeioPagamento.fromValue("credit"));
        assertEquals(MeioPagamento.DEBITO, MeioPagamento.fromValue("cartao_debito"));
        assertEquals(MeioPagamento.DINHEIRO, MeioPagamento.fromValue("cash"));
    }

    @Test
    void shouldNormalizeLegacyStatusAliasesToCanonicalValues() {
        assertEquals(StatusConsulta.EM_ATENDIMENTO, StatusConsulta.fromValue("EM_ANDAMENTO"));
        assertEquals(StatusConsulta.CONCLUIDA, StatusConsulta.fromValue("realizada"));
        assertEquals(StatusConsulta.NAO_COMPARECEU, StatusConsulta.fromValue("NAO_COMPARECEU"));
    }

    @Test
    void shouldNormalizeRoleAliasesToCanonicalApiAndAuthorityValues() {
        AccessRole secretaria = AccessRole.fromValue("role_secretario");

        assertEquals("ROLE_SECRETARIA", secretaria.authority());
        assertEquals("SECRETARIA", secretaria.apiName());
    }
}
