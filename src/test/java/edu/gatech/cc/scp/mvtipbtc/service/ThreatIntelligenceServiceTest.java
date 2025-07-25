package edu.gatech.cc.scp.mvtipbtc.service;

import edu.gatech.cc.scp.mvtipbtc.model.TransactionAnalysis;
import edu.gatech.cc.scp.mvtipbtc.model.User;
import edu.gatech.cc.scp.mvtipbtc.model.Alert;
import edu.gatech.cc.scp.mvtipbtc.repository.TransactionAnalysisRepository;
import edu.gatech.cc.scp.mvtipbtc.repository.AlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ThreatIntelligenceServiceTest {

    @Mock
    private TransactionAnalysisRepository analysisRepository;

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private ChatClient chatClient;

    @InjectMocks
    private ThreatIntelligenceService threatService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User("John", "Doe", "test@example.com", "password");
        testUser.setId(1L);
    }

    @Test
    public void testAnalyzeTransactionNewAddress() {
        String address = "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2"; // Silk Road address
        
        when(analysisRepository.findByAddressAndUser(address, testUser)).thenReturn(Optional.empty());
        when(analysisRepository.save(any(TransactionAnalysis.class))).thenAnswer(invocation -> {
            TransactionAnalysis analysis = invocation.getArgument(0);
            analysis.setId(1L);
            return analysis;
        });

        TransactionAnalysis result = threatService.analyzeTransaction(address, testUser);

        assertNotNull(result);
        assertEquals(address, result.getAddress());
        assertTrue(result.getRiskScore() >= 0 && result.getRiskScore() <= 100);
        assertNotNull(result.getRiskLevel());
        assertEquals(testUser, result.getUser());
        
        verify(analysisRepository).save(any(TransactionAnalysis.class));
    }

    @Test
    public void testAnalyzeTransactionExistingAddress() {
        String address = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa";
        
        TransactionAnalysis existingAnalysis = new TransactionAnalysis();
        existingAnalysis.setId(1L);
        existingAnalysis.setAddress(address);
        existingAnalysis.setRiskScore(50);
        existingAnalysis.setRiskLevel("Medium");
        existingAnalysis.setUser(testUser);

        when(analysisRepository.findByAddressAndUser(address, testUser)).thenReturn(Optional.of(existingAnalysis));

        TransactionAnalysis result = threatService.analyzeTransaction(address, testUser);

        assertNotNull(result);
        assertEquals(existingAnalysis, result);
        verify(analysisRepository, never()).save(any(TransactionAnalysis.class));
    }

    @Test
    public void testAnalyzeHighRiskAddressGeneratesAlert() {
        String highRiskAddress = "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2";
        
        when(analysisRepository.findByAddressAndUser(highRiskAddress, testUser)).thenReturn(Optional.empty());
        when(analysisRepository.save(any(TransactionAnalysis.class))).thenAnswer(invocation -> {
            TransactionAnalysis analysis = invocation.getArgument(0);
            analysis.setId(1L);
            analysis.setRiskScore(95); // High risk score
            analysis.setRiskLevel("Critical");
            return analysis;
        });

        TransactionAnalysis result = threatService.analyzeTransaction(highRiskAddress, testUser);

        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    public void testGetAlertsForUser() {
        List<Alert> mockAlerts = new ArrayList<>();
        Alert alert1 = new Alert("Critical Alert", "High-risk address detected", "Critical risk detected", "Critical", "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2", testUser);
        Alert alert2 = new Alert("High Risk Alert", "Unusual pattern", "Suspicious activity", "High", "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa", testUser);
        mockAlerts.add(alert1);
        mockAlerts.add(alert2);

        when(alertRepository.findByUserOrderByTimestampDesc(testUser)).thenReturn(mockAlerts);

        List<Alert> result = threatService.getAlertsForUser(testUser);

        assertEquals(2, result.size());
        assertEquals("Critical Alert", result.get(0).getType());
        assertEquals("High Risk Alert", result.get(1).getType());
    }

    @Test
    public void testGetAnalysesForUser() {
        List<TransactionAnalysis> mockAnalyses = new ArrayList<>();
        TransactionAnalysis analysis1 = new TransactionAnalysis();
        analysis1.setAddress("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2");
        analysis1.setRiskScore(95);
        analysis1.setUser(testUser);
        
        TransactionAnalysis analysis2 = new TransactionAnalysis();
        analysis2.setAddress("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");
        analysis2.setRiskScore(30);
        analysis2.setUser(testUser);
        
        mockAnalyses.add(analysis1);
        mockAnalyses.add(analysis2);

        when(analysisRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(mockAnalyses);

        List<TransactionAnalysis> result = threatService.getAnalysesForUser(testUser);

        assertEquals(2, result.size());
        assertEquals("1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2", result.get(0).getAddress());
        assertEquals(95, result.get(0).getRiskScore());
    }

    @Test
    public void testAnalyzeKnownMaliciousAddresses() {
        // Test with known malicious Bitcoin addresses
        String[] maliciousAddresses = {
            "1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2", // Silk Road
            "1FeexV6bAHb8ybZjqQMjJrcCrHGW9sb6uF", // BitFinex hack
            "1NDyJtNTjmwk5xPNhjgAMu4HDHigtobu1s"  // Another known malicious address
        };

        for (String address : maliciousAddresses) {
            when(analysisRepository.findByAddressAndUser(address, testUser)).thenReturn(Optional.empty());
            when(analysisRepository.save(any(TransactionAnalysis.class))).thenAnswer(invocation -> {
                TransactionAnalysis analysis = invocation.getArgument(0);
                analysis.setId(1L);
                return analysis;
            });

            TransactionAnalysis result = threatService.analyzeTransaction(address, testUser);

            assertNotNull(result);
            assertEquals(address, result.getAddress());
            // Should have high risk score for known malicious addresses
            assertTrue(result.getRiskScore() > 50, "Risk score should be high for malicious address: " + address);
        }
    }
}

