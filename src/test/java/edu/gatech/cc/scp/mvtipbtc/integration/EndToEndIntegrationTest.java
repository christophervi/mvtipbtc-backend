package edu.gatech.cc.scp.mvtipbtc.integration;

import edu.gatech.cc.scp.mvtipbtc.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class EndToEndIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String authToken;

    @Test
    @Order(1)
    public void testCompleteUserRegistrationFlow() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("John", "Doe", "john.doe@example.com", "password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    @Order(2)
    public void testCompleteUserLoginFlow() throws Exception {
        LoginRequest loginRequest = new LoginRequest("john.doe@example.com", "password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value("john.doe@example.com"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseContent, LoginResponse.class);
        authToken = loginResponse.getToken();
    }

    @Test
    @Order(3)
    public void testAnalyzeMaliciousBitcoinAddress() throws Exception {
        // Test with known Silk Road address
        String silkRoadAddress = "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2";
        TransactionAnalysisRequest request = new TransactionAnalysisRequest(silkRoadAddress);

        mockMvc.perform(post("/api/analysis/transaction")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(silkRoadAddress))
                .andExpect(jsonPath("$.riskScore").exists())
                .andExpect(jsonPath("$.riskLevel").exists())
                .andExpect(jsonPath("$.analysis").exists());
    }

    @Test
    @Order(4)
    public void testAnalyzeMixingServiceAddress() throws Exception {
        // Test with a mixing service pattern address
        String mixingAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa";
        TransactionAnalysisRequest request = new TransactionAnalysisRequest(mixingAddress);

        mockMvc.perform(post("/api/analysis/transaction")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(mixingAddress))
                .andExpect(jsonPath("$.mixingProbability").exists())
                .andExpect(jsonPath("$.illicitFundSources").exists())
                .andExpect(jsonPath("$.darkMarketConnections").exists());
    }

    @Test
    @Order(5)
    public void testGetUserAlerts() throws Exception {
        mockMvc.perform(get("/api/alerts")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(6)
    public void testGenerateReport() throws Exception {
        String address = "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2";
        ReportGenerationRequest request = new ReportGenerationRequest(address);

        mockMvc.perform(post("/api/reports/generate")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportUrl").exists());
    }

    @Test
    @Order(7)
    public void testGetUserReports() throws Exception {
        mockMvc.perform(get("/api/reports")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(8)
    public void testGetRealTimeStats() throws Exception {
        mockMvc.perform(get("/api/monitoring/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mempool").exists())
                .andExpect(jsonPath("$.fees").exists())
                .andExpect(jsonPath("$.network").exists());
    }

    @Test
    @Order(9)
    public void testGetTransactionFlow() throws Exception {
        String address = "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2";

        mockMvc.perform(get("/api/analysis/flow/" + address)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes").isArray())
                .andExpect(jsonPath("$.edges").isArray());
    }

    @Test
    @Order(10)
    public void testUnauthorizedAccess() throws Exception {
        TransactionAnalysisRequest request = new TransactionAnalysisRequest("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");

        mockMvc.perform(post("/api/analysis/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(11)
    public void testInvalidToken() throws Exception {
        TransactionAnalysisRequest request = new TransactionAnalysisRequest("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");

        mockMvc.perform(post("/api/analysis/transaction")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

