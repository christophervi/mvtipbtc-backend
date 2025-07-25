package edu.gatech.cc.scp.mvtipbtc.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.gatech.cc.scp.mvtipbtc.dto.LoginRequest;
import edu.gatech.cc.scp.mvtipbtc.dto.LoginResponse;
import edu.gatech.cc.scp.mvtipbtc.dto.RegisterRequest;
import edu.gatech.cc.scp.mvtipbtc.dto.UserDto;
import edu.gatech.cc.scp.mvtipbtc.service.AuthService;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    private final AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;
    
    public AuthControllerTest(AuthService authService) {
		super();
		this.authService = authService;
	}
    
	@Test
    public void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        UserDto userDto = new UserDto(1L, "John", "Doe", "test@example.com");
        LoginResponse response = new LoginResponse("mock-jwt-token", userDto);

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    public void testLoginFailure() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "wrongpassword");

        when(authService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("John", "Doe", "test@example.com", "password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void testRegisterEmailExists() throws Exception {
        RegisterRequest request = new RegisterRequest("John", "Doe", "existing@example.com", "password123");

        doThrow(new RuntimeException("Email already exists")).when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists"));
    }
}

