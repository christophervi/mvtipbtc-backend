package edu.gatech.cc.scp.mvtipbtc.integration;

import edu.gatech.cc.scp.mvtipbtc.model.User;
import edu.gatech.cc.scp.mvtipbtc.repository.UserRepository;
import edu.gatech.cc.scp.mvtipbtc.service.AuthService;
import edu.gatech.cc.scp.mvtipbtc.config.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class ComponentIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User testUser;
    private String authToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Create test user
        testUser = new User();
        //testUser.setUsername("integrationtest");
        testUser.setEmail("integration@test.com");
        testUser.setPassword(passwordEncoder.encode("testpassword"));
        testUser.setFirstName("Integration");
        testUser.setLastName("Test");
        //testUser.setOrganization("Test Org");
        //testUser.setRole("User");
        //testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        
        testUser = userRepository.save(testUser);
        
        // Generate auth token
        authToken = jwtUtil.generateToken(testUser.getEmail());
    }

    @Test
    void testCompleteAlertsWorkflow() throws Exception {
        // Test getting alerts
        mockMvc.perform(get("/api/alerts")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Test getting alert stats
        mockMvc.perform(get("/api/alerts/stats")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").exists());

        // Test marking all alerts as read
        mockMvc.perform(post("/api/alerts/mark-all-read")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All alerts marked as read"));
    }

    @Test
    void testCompleteReportsWorkflow() throws Exception {
        // Test getting reports
        mockMvc.perform(get("/api/reports")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Test generating a report
        Map<String, String> reportRequest = new HashMap<>();
        reportRequest.put("address", "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");

        mockMvc.perform(post("/api/reports/generate")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportUrl").exists());
    }

    @Test
    void testCompleteSettingsWorkflow() throws Exception {
        // Test getting user profile
        mockMvc.perform(get("/api/settings/profile")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("integration@test.com"));

        // Test updating profile
        Map<String, String> profileUpdate = new HashMap<>();
        profileUpdate.put("firstName", "Updated");
        profileUpdate.put("lastName", "Name");

        mockMvc.perform(put("/api/settings/profile")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile updated successfully"));

        // Test getting notification settings
        mockMvc.perform(get("/api/settings/notifications")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailAlerts").exists());

        // Test getting security settings
        mockMvc.perform(get("/api/settings/security")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.twoFactorAuth").exists());
    }

    @Test
    void testTransactionAnalysisWorkflow() throws Exception {
        // Test analyzing a known malicious address
        Map<String, String> analysisRequest = new HashMap<>();
        analysisRequest.put("address", "1FeexV6bAHb8ybZjqQMjJrcCrHGW9sb6uF");

        mockMvc.perform(post("/api/analysis/transaction")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(analysisRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("1FeexV6bAHb8ybZjqQMjJrcCrHGW9sb6uF"))
                .andExpect(jsonPath("$.riskScore").exists())
                .andExpect(jsonPath("$.riskLevel").exists());

        // Test getting transaction flow
        mockMvc.perform(get("/api/analysis/flow/1FeexV6bAHb8ybZjqQMjJrcCrHGW9sb6uF")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes").exists())
                .andExpect(jsonPath("$.edges").exists());
    }

    @Test
    void testMonitoringEndpoints() throws Exception {
        // Test getting real-time stats
        mockMvc.perform(get("/api/monitoring/stats")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.networkHashRate").exists())
                .andExpect(jsonPath("$.totalTransactions").exists());

        // Test getting network status
        mockMvc.perform(get("/api/monitoring/network")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blockHeight").exists())
                .andExpect(jsonPath("$.difficulty").exists());
    }

    @Test
    void testAuthenticationFlow() throws Exception {
        // Test login
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", "integration@test.com");
        loginRequest.put("password", "testpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value("integration@test.com"));

        // Test token validation
        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void testErrorHandling() throws Exception {
        // Test with invalid token
        mockMvc.perform(get("/api/alerts")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isBadRequest());

        // Test with missing authorization header
        mockMvc.perform(get("/api/alerts"))
                .andExpect(status().isUnauthorized());

        // Test analyzing invalid address
        Map<String, String> invalidRequest = new HashMap<>();
        invalidRequest.put("address", "invalid-address");

        mockMvc.perform(post("/api/analysis/transaction")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCrossOriginRequests() throws Exception {
        // Test CORS headers are present
        mockMvc.perform(options("/api/alerts")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
}

