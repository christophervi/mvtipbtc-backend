package edu.gatech.cc.scp.mvtipbtc.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.gatech.cc.scp.mvtipbtc.config.JwtUtil;
import edu.gatech.cc.scp.mvtipbtc.dto.PasswordChangeRequest;
import edu.gatech.cc.scp.mvtipbtc.dto.ProfileUpdateRequest;
import edu.gatech.cc.scp.mvtipbtc.model.User;
import edu.gatech.cc.scp.mvtipbtc.service.AuthService;
import edu.gatech.cc.scp.mvtipbtc.service.SettingsService;

@WebMvcTest(SettingsController.class)
public class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    private final AuthService authService;
    
    private final SettingsService settingsService;
    
    private final JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private String validToken = "valid-jwt-token";
    
    public SettingsControllerTest(AuthService authService, SettingsService settingsService, JwtUtil jwtUtil) {
		super();
		this.authService = authService;
		this.settingsService = settingsService;
		this.jwtUtil = jwtUtil;
	}
    
	@BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        //testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        //testUser.setOrganization("Test Org");
        //testUser.setRole("User");
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetUserProfile() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", 1L);
        profile.put("username", "testuser");
        profile.put("email", "test@example.com");
        profile.put("firstName", "Test");
        profile.put("lastName", "User");
        
        when(settingsService.getUserProfile(testUser)).thenReturn(profile);

        mockMvc.perform(get("/api/settings/profile")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUpdateProfile() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(settingsService.updateUserProfile(eq(testUser), any(ProfileUpdateRequest.class))).thenReturn(true);

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setFirstName("Updated");
        request.setLastName("Name");

        mockMvc.perform(put("/api/settings/profile")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile updated successfully"));
    }

    @Test
    void testChangePassword() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(settingsService.changePassword(eq(testUser), any(PasswordChangeRequest.class))).thenReturn(true);

        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("oldpassword");
        request.setNewPassword("newpassword");

        mockMvc.perform(post("/api/settings/password")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    @Test
    void testGetNotificationSettings() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
        
        Map<String, Object> settings = new HashMap<>();
        settings.put("emailAlerts", true);
        settings.put("pushNotifications", true);
        settings.put("criticalAlerts", true);
        
        when(settingsService.getNotificationSettings(testUser)).thenReturn(settings);

        mockMvc.perform(get("/api/settings/notifications")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAlerts").value(true))
                .andExpect(jsonPath("$.criticalAlerts").value(true));
    }

    @Test
    void testExportUserData() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(settingsService.exportUserData(testUser)).thenReturn("https://example.com/export.json");

        mockMvc.perform(post("/api/settings/export-data")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Data export initiated"))
                .andExpect(jsonPath("$.exportUrl").value("https://example.com/export.json"));
    }

    @Test
    void testClearUserCache() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);

        mockMvc.perform(post("/api/settings/clear-cache")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User cache cleared successfully"));
    }

    @Test
    void testDeleteAccount() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(settingsService.deleteUserAccount(testUser)).thenReturn(true);

        mockMvc.perform(delete("/api/settings/account")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account deletion initiated"));
    }

    @Test
    void testInvalidToken() throws Exception {
        when(jwtUtil.getUsernameFromToken(anyString())).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(get("/api/settings/profile")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isBadRequest());
    }
}

