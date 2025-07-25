package edu.gatech.cc.scp.mvtipbtc.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.gatech.cc.scp.mvtipbtc.dto.DashboardSummaryResponse;
import edu.gatech.cc.scp.mvtipbtc.dto.RecentActivityResponse;
import edu.gatech.cc.scp.mvtipbtc.dto.RiskCategoryResponse;
import edu.gatech.cc.scp.mvtipbtc.dto.SystemStatusResponse;
import edu.gatech.cc.scp.mvtipbtc.service.ThreatIntelligenceService;

@RestController
@RequestMapping("/api/home")
public class HomeController {
	
	@Autowired
    private ThreatIntelligenceService threatIntelligenceService;
	
    /*public HomeController(ThreatIntelligenceService threatIntelligenceService) {
        this.threatIntelligenceService = threatIntelligenceService;
    }*/
    
    @GetMapping("/summary")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        DashboardSummaryResponse summary = new DashboardSummaryResponse();
        
        // Get alert counts by risk level
        summary.setCriticalAlerts(threatIntelligenceService.getAlertCountByRiskLevel("Critical"));
        summary.setHighRiskAlerts(threatIntelligenceService.getAlertCountByRiskLevel("High"));
        summary.setMediumRiskAlerts(threatIntelligenceService.getAlertCountByRiskLevel("Medium"));
        summary.setLowRiskAlerts(threatIntelligenceService.getAlertCountByRiskLevel("Low"));
        
        // Get transaction statistics
        summary.setTotalTransactionsAnalyzed(threatIntelligenceService.getTotalTransactionsAnalyzed());
        summary.setHighRiskTransactions(threatIntelligenceService.getHighRiskTransactionsCount());
        summary.setAverageRiskScore(threatIntelligenceService.getAverageRiskScore());
        
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/recent-activity")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RecentActivityResponse> getRecentActivity() {
        RecentActivityResponse response = new RecentActivityResponse();
        response.setActivities(threatIntelligenceService.getRecentActivities(10));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/risk-categories")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<RiskCategoryResponse> getRiskCategories() {
        RiskCategoryResponse response = new RiskCategoryResponse();
        
        // Sample data
        Map<String, Integer> categories = new HashMap<>();
        categories.put("Money Laundering", 35);
        categories.put("Ransomware", 25);
        categories.put("Dark Markets", 20);
        categories.put("Scams", 15);
        categories.put("Fraud", 5);
        
        response.setCategories(categories);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/system-status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SystemStatusResponse> getSystemStatus() {
        SystemStatusResponse response = new SystemStatusResponse();
        
        response.setApiStatus("Operational");
        response.setDatabaseStatus("Connected");
        response.setLastUpdated(System.currentTimeMillis());
        response.setMemoryUsage(85);
        response.setCpuUsage(42);
        
        return ResponseEntity.ok(response);
    }
}

