package edu.gatech.cc.scp.mvtipbtc.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.gatech.cc.scp.mvtipbtc.config.JwtUtil;
import edu.gatech.cc.scp.mvtipbtc.dto.TransactionAnalysisRequest;
import edu.gatech.cc.scp.mvtipbtc.model.TransactionAnalysis;
import edu.gatech.cc.scp.mvtipbtc.model.User;
import edu.gatech.cc.scp.mvtipbtc.service.AuthService;
import edu.gatech.cc.scp.mvtipbtc.service.ThreatIntelligenceService;

@WebMvcTest(AnalysisController.class)
public class AnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    private final ThreatIntelligenceService threatService;
    
    private final AuthService authService;
    
    private final JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;
    
    public AnalysisControllerTest(ThreatIntelligenceService threatService, AuthService authService, JwtUtil jwtUtil) {
		super();
		this.threatService = threatService;
		this.authService = authService;
		this.jwtUtil = jwtUtil;
	}
    
	@Test
    public void testAnalyzeTransactionSuccess() throws Exception {
        // Known malicious Bitcoin address for testing
        String maliciousAddress = "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2"; // Silk Road address
        
        TransactionAnalysisRequest request = new TransactionAnalysisRequest(maliciousAddress);
        
        User mockUser = new User("John", "Doe", "test@example.com", "password");
        mockUser.setId(1L);
        
        TransactionAnalysis mockAnalysis = new TransactionAnalysis();
        mockAnalysis.setId(1L);
        mockAnalysis.setAddress(maliciousAddress);
        mockAnalysis.setRiskScore(95);
        mockAnalysis.setRiskLevel("Critical");
        mockAnalysis.setTotalReceived(new BigDecimal("1000.50"));
        mockAnalysis.setTotalSent(new BigDecimal("1000.50"));
        mockAnalysis.setCurrentBalance(new BigDecimal("0.00"));
        mockAnalysis.setNumberOfTransactions(500);
        mockAnalysis.setAnalysis("This address is associated with the Silk Road marketplace and poses critical risk.");
        mockAnalysis.setMixingProbability(95);
        mockAnalysis.setIllicitFundSources(98);
        mockAnalysis.setDarkMarketConnections(100);
        mockAnalysis.setUser(mockUser);
        mockAnalysis.setCreatedAt(LocalDateTime.now());

        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("test@example.com");
        when(authService.getUserByEmail("test@example.com")).thenReturn(mockUser);
        when(threatService.analyzeTransaction(maliciousAddress, mockUser)).thenReturn(mockAnalysis);

        mockMvc.perform(post("/api/analysis/transaction")
                .header("Authorization", "Bearer mock-jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(maliciousAddress))
                .andExpect(jsonPath("$.riskScore").value(95))
                .andExpect(jsonPath("$.riskLevel").value("Critical"))
                .andExpect(jsonPath("$.mixingProbability").value(95))
                .andExpect(jsonPath("$.illicitFundSources").value(98))
                .andExpect(jsonPath("$.darkMarketConnections").value(100));
    }

    @Test
    public void testAnalyzeTransactionWithMixingService() throws Exception {
        // Known mixing service address
        String mixingAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"; // Genesis block address (for testing)
        
        TransactionAnalysisRequest request = new TransactionAnalysisRequest(mixingAddress);
        
        User mockUser = new User("Jane", "Smith", "jane@example.com", "password");
        mockUser.setId(2L);
        
        TransactionAnalysis mockAnalysis = new TransactionAnalysis();
        mockAnalysis.setId(2L);
        mockAnalysis.setAddress(mixingAddress);
        mockAnalysis.setRiskScore(78);
        mockAnalysis.setRiskLevel("High");
        mockAnalysis.setTotalReceived(new BigDecimal("50.25"));
        mockAnalysis.setTotalSent(new BigDecimal("50.25"));
        mockAnalysis.setCurrentBalance(new BigDecimal("0.00"));
        mockAnalysis.setNumberOfTransactions(150);
        mockAnalysis.setAnalysis("This address shows patterns consistent with coin mixing activities.");
        mockAnalysis.setMixingProbability(85);
        mockAnalysis.setIllicitFundSources(60);
        mockAnalysis.setDarkMarketConnections(45);
        mockAnalysis.setUser(mockUser);
        mockAnalysis.setCreatedAt(LocalDateTime.now());

        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("jane@example.com");
        when(authService.getUserByEmail("jane@example.com")).thenReturn(mockUser);
        when(threatService.analyzeTransaction(mixingAddress, mockUser)).thenReturn(mockAnalysis);

        mockMvc.perform(post("/api/analysis/transaction")
                .header("Authorization", "Bearer mock-jwt-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(mixingAddress))
                .andExpect(jsonPath("$.riskScore").value(78))
                .andExpect(jsonPath("$.riskLevel").value("High"))
                .andExpect(jsonPath("$.mixingProbability").value(85));
    }

    @Test
    public void testAnalyzeTransactionUnauthorized() throws Exception {
        TransactionAnalysisRequest request = new TransactionAnalysisRequest("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");

        when(jwtUtil.getUsernameFromToken(anyString())).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(post("/api/analysis/transaction")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetTransactionFlow() throws Exception {
        String address = "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2";

        mockMvc.perform(get("/api/analysis/flow/" + address)
                .header("Authorization", "Bearer mock-jwt-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes").isArray())
                .andExpect(jsonPath("$.edges").isArray());
    }
}

