package com.makeup.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;

import com.fasterxml.jackson.databind.ObjectMapper;

class JwtAuthenticationFilterTest {

    @Test
    void disabledUserReturnsForbiddenWithErrorCause() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                mock(AuthenticationManager.class), mock(JwtService.class));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.unsuccessfulAuthentication(request, response, new DisabledException("disabled"));

        assertEquals(403, response.getStatus());
        ObjectMapper objectMapper = new ObjectMapper();
        Object body = objectMapper.readValue(response.getContentAsString(), Object.class);
        assertTrue(body.toString().contains("errorCause=disabled"));
        assertTrue(body.toString().contains("L'utilisateur est désactivé !"));
    }
}
