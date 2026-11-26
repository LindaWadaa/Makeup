package com.makeup.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final ServletWebRequest webRequest = new ServletWebRequest(new MockHttpServletRequest());

    @Test
    void duplicateEmailUsesUserEmailAlreadyExistsCode() {
        ResponseEntity<ErrorDetails> response = handler.handleEmailAlreadyExistsException(
                new EmailAlreadyExistsException("email déjà existant!"), webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("USER_EMAIL_ALREADY_EXISTS", response.getBody().getErrorCode());
        assertEquals("email déjà existant!", response.getBody().getMessage());
    }

    @Test
    void invalidTokenUsesInvalidTokenCode() {
        ResponseEntity<ErrorDetails> response = handler.handleInvalidTokenException(
                new InvalidTokenException("Invalid Token"), webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("INVALID_TOKEN", response.getBody().getErrorCode());
    }

    @Test
    void expiredTokenUsesExpiredTokenCode() {
        ResponseEntity<ErrorDetails> response = handler.handleExpiredTokenException(
                new ExpiredTokenException("expired Token"), webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("EXPIRED_TOKEN", response.getBody().getErrorCode());
    }
}
