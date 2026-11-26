package com.makeup.RestControllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makeup.entities.User;
import com.makeup.register.RegistationRequest;
import com.makeup.service.UserService;

class UserRestControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private UserService userService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserRestController(userService)).build();
    }

    @Test
    void registerCallsServiceAndReturnsCreatedUser() throws Exception {
        User user = new User("nadh", "encoded");
        user.setEmail("nadh@example.com");
        user.setEnabled(false);
        when(userService.registerUser(any(RegistationRequest.class))).thenReturn(user);

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new RegistationRequest("nadh", "pass123", "nadh@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("nadh"))
                .andExpect(jsonPath("$.email").value("nadh@example.com"))
                .andExpect(jsonPath("$.enabled").value(false));

        verify(userService).registerUser(any(RegistationRequest.class));
    }

    @Test
    void verifyEmailCallsServiceWithPathToken() throws Exception {
        User user = new User("nadh", "encoded");
        user.setEnabled(true);
        when(userService.validateToken("123456")).thenReturn(user);

        mockMvc.perform(get("/verifyEmail/123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true));

        verify(userService).validateToken("123456");
    }
}
