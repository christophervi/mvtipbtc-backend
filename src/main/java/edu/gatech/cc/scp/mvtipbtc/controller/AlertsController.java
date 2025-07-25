package edu.gatech.cc.scp.mvtipbtc.controller;

import edu.gatech.cc.scp.mvtipbtc.service.ThreatIntelligenceService;
import edu.gatech.cc.scp.mvtipbtc.service.AuthService;
import edu.gatech.cc.scp.mvtipbtc.model.Alert;
import edu.gatech.cc.scp.mvtipbtc.model.User;
import edu.gatech.cc.scp.mvtipbtc.config.JwtUtil;
import edu.gatech.cc.scp.mvtipbtc.dto.AlertUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/alerts")
//@CrossOrigin(origins = "*")
public class AlertsController {
    
    @Autowired
    private ThreatIntelligenceService threatService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping
    public ResponseEntity<List<Alert>> getAlerts(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            List<Alert> alerts = threatService.getAlertsForUser(user);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/filter")
    public ResponseEntity<List<Alert>> getFilteredAlerts(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String alertType,
            @RequestParam(required = false) String status) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            List<Alert> alerts = threatService.getFilteredAlerts(user, riskLevel, alertType, status);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlert(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            Alert alert = threatService.getAlertById(id, user);
            if (alert != null) {
                return ResponseEntity.ok(alert);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> updateAlertStatus(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody AlertUpdateRequest request) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            boolean updated = threatService.updateAlertStatus(id, request.getStatus(), user);
            
            Map<String, String> response = new HashMap<>();
            if (updated) {
                response.put("message", "Alert status updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Alert not found or access denied");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update alert status");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> dismissAlert(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            boolean dismissed = threatService.dismissAlert(id, user);
            
            Map<String, String> response = new HashMap<>();
            if (dismissed) {
                response.put("message", "Alert dismissed successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Alert not found or access denied");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to dismiss alert");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAlertStats(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            Map<String, Object> stats = threatService.getAlertStats(user);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/mark-all-read")
    public ResponseEntity<Map<String, String>> markAllAlertsAsRead(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            threatService.markAllAlertsAsRead(user);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "All alerts marked as read");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to mark alerts as read");
            return ResponseEntity.badRequest().body(response);
        }
    }
}

