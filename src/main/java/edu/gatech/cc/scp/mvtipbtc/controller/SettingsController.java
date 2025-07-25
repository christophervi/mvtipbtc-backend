package edu.gatech.cc.scp.mvtipbtc.controller;

import edu.gatech.cc.scp.mvtipbtc.service.AuthService;
import edu.gatech.cc.scp.mvtipbtc.service.SettingsService;
import edu.gatech.cc.scp.mvtipbtc.model.User;
import edu.gatech.cc.scp.mvtipbtc.config.JwtUtil;
import edu.gatech.cc.scp.mvtipbtc.dto.ProfileUpdateRequest;
import edu.gatech.cc.scp.mvtipbtc.dto.PasswordChangeRequest;
import edu.gatech.cc.scp.mvtipbtc.dto.NotificationSettingsRequest;
import edu.gatech.cc.scp.mvtipbtc.dto.SecuritySettingsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class SettingsController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private SettingsService settingsService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            Map<String, Object> profile = settingsService.getUserProfile(user);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<Map<String, String>> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ProfileUpdateRequest request) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            boolean updated = settingsService.updateUserProfile(user, request);
            
            Map<String, String> response = new HashMap<>();
            if (updated) {
                response.put("message", "Profile updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Failed to update profile");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update profile");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PasswordChangeRequest request) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            boolean changed = settingsService.changePassword(user, request);
            
            Map<String, String> response = new HashMap<>();
            if (changed) {
                response.put("message", "Password changed successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Current password is incorrect");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to change password");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> getNotificationSettings(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            Map<String, Object> settings = settingsService.getNotificationSettings(user);
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/notifications")
    public ResponseEntity<Map<String, String>> updateNotificationSettings(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody NotificationSettingsRequest request) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            boolean updated = settingsService.updateNotificationSettings(user, request);
            
            Map<String, String> response = new HashMap<>();
            if (updated) {
                response.put("message", "Notification settings updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Failed to update notification settings");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update notification settings");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/security")
    public ResponseEntity<Map<String, Object>> getSecuritySettings(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            Map<String, Object> settings = settingsService.getSecuritySettings(user);
            return ResponseEntity.ok(settings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/security")
    public ResponseEntity<Map<String, String>> updateSecuritySettings(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SecuritySettingsRequest request) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            boolean updated = settingsService.updateSecuritySettings(user, request);
            
            Map<String, String> response = new HashMap<>();
            if (updated) {
                response.put("message", "Security settings updated successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Failed to update security settings");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to update security settings");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/export-data")
    public ResponseEntity<Map<String, String>> exportUserData(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            String exportUrl = settingsService.exportUserData(user);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Data export initiated");
            response.put("exportUrl", exportUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to export data");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/clear-cache")
    public ResponseEntity<Map<String, String>> clearUserCache(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            settingsService.clearUserCache(user);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User cache cleared successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to clear cache");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/account")
    public ResponseEntity<Map<String, String>> deleteAccount(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getUsernameFromToken(token);
            User user = authService.getUserByEmail(email);
            
            boolean deleted = settingsService.deleteUserAccount(user);
            
            Map<String, String> response = new HashMap<>();
            if (deleted) {
                response.put("message", "Account deletion initiated");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Failed to delete account");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to delete account");
            return ResponseEntity.badRequest().body(response);
        }
    }
}

