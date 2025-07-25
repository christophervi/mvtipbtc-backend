package edu.gatech.cc.scp.mvtipbtc.controller;

import edu.gatech.cc.scp.mvtipbtc.dto.DashboardSummaryResponse;
import edu.gatech.cc.scp.mvtipbtc.dto.RecentActivityResponse;
import edu.gatech.cc.scp.mvtipbtc.dto.RiskCategoryResponse;
import edu.gatech.cc.scp.mvtipbtc.dto.SystemStatusResponse;
import edu.gatech.cc.scp.mvtipbtc.service.ThreatIntelligenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HomeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ThreatIntelligenceService threatIntelligenceService;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
    }

    @Test
    public void testGetDashboardSummary() throws Exception {
        // Mock service responses
        when(threatIntelligenceService.getAlertCountByRiskLevel("Critical")).thenReturn(2);
        when(threatIntelligenceService.getAlertCountByRiskLevel("High")).thenReturn(5);
        when(threatIntelligenceService.getAlertCountByRiskLevel("Medium")).thenReturn(8);
        when(threatIntelligenceService.getAlertCountByRiskLevel("Low")).thenReturn(12);
        when(threatIntelligenceService.getTotalTransactionsAnalyzed()).thenReturn(1245);
        when(threatIntelligenceService.getHighRiskTransactionsCount()).thenReturn(87);
        when(threatIntelligenceService.getAverageRiskScore()).thenReturn(42.7);

        // Perform GET request and validate response
        mockMvc.perform(get("/api/home/summary")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.criticalAlerts").value(2))
                .andExpect(jsonPath("$.highRiskAlerts").value(5))
                .andExpect(jsonPath("$.mediumRiskAlerts").value(8))
                .andExpect(jsonPath("$.lowRiskAlerts").value(12))
                .andExpect(jsonPath("$.totalTransactionsAnalyzed").value(1245))
                .andExpect(jsonPath("$.highRiskTransactions").value(87))
                .andExpect(jsonPath("$.averageRiskScore").value(42.7));
    }

    @Test
    public void testGetRecentActivity() throws Exception {
        // Mock service response
        List<RecentActivityResponse.ActivityItem> activities = new ArrayList<>();
        RecentActivityResponse.ActivityItem activity = new RecentActivityResponse.ActivityItem();
        activity.setType("Alert");
        activity.setDescription("Test alert");
        activity.setTimestamp(System.currentTimeMillis());
        activity.setAddress("1FeexV6bAHb8ybZjqQMjJrcCrHGW9sb6uF");
        activity.setRiskLevel("Critical");
        activities.add(activity);
        
        when(threatIntelligenceService.getRecentActivities(anyInt())).thenReturn(activities);

        // Perform GET request and validate response
        mockMvc.perform(get("/api/home/recent-activity")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activities[0].type").value("Alert"))
                .andExpect(jsonPath("$.activities[0].description").value("Test alert"))
                .andExpect(jsonPath("$.activities[0].address").value("1FeexV6bAHb8ybZjqQMjJrcCrHGW9sb6uF"))
                .andExpect(jsonPath("$.activities[0].riskLevel").value("Critical"));
    }

    @Test
    public void testGetRiskCategories() throws Exception {
        // Perform GET request and validate response
        mockMvc.perform(get("/api/home/risk-categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.['Money Laundering']").value(35))
                .andExpect(jsonPath("$.categories.Ransomware").value(25))
                .andExpect(jsonPath("$.categories.['Dark Markets']").value(20))
                .andExpect(jsonPath("$.categories.Scams").value(15))
                .andExpect(jsonPath("$.categories.Fraud").value(5));
    }

    @Test
    public void testGetSystemStatus() throws Exception {
        // Perform GET request and validate response
        mockMvc.perform(get("/api/home/system-status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apiStatus").value("Operational"))
                .andExpect(jsonPath("$.databaseStatus").value("Connected"))
                .andExpect(jsonPath("$.memoryUsage").value(85))
                .andExpect(jsonPath("$.cpuUsage").value(42));
    }
}

