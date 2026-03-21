package com.inflowia.medicflow.config;

import com.inflowia.medicflow.exception.ControllerExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void shouldReuseIncomingCorrelationId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, "abc-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals("abc-123", request.getAttribute(ControllerExceptionHandler.TRACE_ID_ATTRIBUTE));
        assertEquals("abc-123", response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER));
    }

    @Test
    void shouldGenerateCorrelationIdWhenMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        String traceId = (String) request.getAttribute(ControllerExceptionHandler.TRACE_ID_ATTRIBUTE);
        assertNotNull(traceId);
        assertEquals(traceId, response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER));
    }
}
