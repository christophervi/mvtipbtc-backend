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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.gatech.cc.scp.mvtipbtc.config.JwtUtil;
import edu.gatech.cc.scp.mvtipbtc.model.Alert;
import edu.gatech.cc.scp.mvtipbtc.model.User;
import edu.gatech.cc.scp.mvtipbtc.service.AuthService;
import edu.gatech.cc.scp.mvtipbtc.service.ThreatIntelligenceService;

@WebMvcTest(AlertsController.class)
public class AlertsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    private final ThreatIntelligenceService threatService;
    
    private final AuthService authService;
    
    private final JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private List<Alert> testAlerts;
    private String validToken = "valid-jwt-token";
    
    public AlertsControllerTest(MockMvc mockMvc, ThreatIntelligenceService threatService, AuthService authService,
			JwtUtil jwtUtil) {
		super();
		this.mockMvc = mockMvc;
		this.threatService = threatService;
		this.authService = authService;
		this.jwtUtil = jwtUtil;
	}

	@BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        //testUser.setUsername("testuser");
        testUser.setFirstName("testuser");

        Alert alert1 = new Alert();
        alert1.setId(1L);
        alert1.setType("Critical Alert");
        alert1.setTitle("High Risk Transaction");
        alert1.setDescription("Suspicious activity detected");
        alert1.setRiskLevel("Critical");
        //alert1.setStatus("Active");
        alert1.setTimestamp(LocalDateTime.now());
        alert1.setUser(testUser);

        Alert alert2 = new Alert();
        alert2.setId(2L);
        alert2.setType("High Risk Alert");
        alert2.setTitle("Unusual Pattern");
        alert2.setDescription("Unusual transaction pattern");
        alert2.setRiskLevel("High");
        //alert2.setStatus("Active");
        alert2.setTimestamp(LocalDateTime.now());
        alert2.setUser(testUser);

        testAlerts = Arrays.asList(alert1, alert2);
    }

    @Test
    void testGetAlerts() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(threatService.getAlertsForUser(testUser)).thenReturn(testAlerts);

        mockMvc.perform(get("/api/alerts")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("Critical Alert"))
                .andExpect(jsonPath("$[1].type").value("High Risk Alert"));
    }

    @Test
    void testGetFilteredAlerts() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(threatService.getFilteredAlerts(eq(testUser), eq("Critical"), any(), any()))
                .thenReturn(Arrays.asList(testAlerts.get(0)));

        mockMvc.perform(get("/api/alerts/filter")
                .param("riskLevel", "Critical")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].riskLevel").value("Critical"));
    }

    @Test
    void testGetAlert() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(threatService.getAlertById(1L, testUser)).thenReturn(testAlerts.get(0));

        mockMvc.perform(get("/api/alerts/1")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("Critical Alert"));
    }

    @Test
    void testUpdateAlertStatus() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(threatService.updateAlertStatus(1L, "Resolved", testUser)).thenReturn(true);

        Map<String, String> request = new HashMap<>();
        request.put("status", "Resolved");

        mockMvc.perform(put("/api/alerts/1/status")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Alert status updated successfully"));
    }

    @Test
    void testDismissAlert() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(threatService.dismissAlert(1L, testUser)).thenReturn(true);

        mockMvc.perform(delete("/api/alerts/1")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Alert dismissed successfully"));
    }

    @Test
    void testGetAlertStats() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("critical", 1L);
        stats.put("high", 1L);
        stats.put("medium", 0L);
        stats.put("active", 2L);
        stats.put("total", 2);
        
        when(threatService.getAlertStats(testUser)).thenReturn(stats);

        mockMvc.perform(get("/api/alerts/stats")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.critical").value(1))
                .andExpect(jsonPath("$.high").value(1))
                .andExpect(jsonPath("$.total").value(2));
    }

    @Test
    void testMarkAllAlertsAsRead() throws Exception {
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(testUser);

        mockMvc.perform(post("/api/alerts/mark-all-read")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All alerts marked as read"));
    }

    @Test
    void testGetAlertsWithInvalidToken() throws Exception {
        when(jwtUtil.getUsernameFromToken(anyString())).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(get("/api/alerts")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isBadRequest());
    }
}

