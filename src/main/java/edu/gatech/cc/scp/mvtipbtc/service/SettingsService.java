package edu.gatech.cc.scp.mvtipbtc.service;

import edu.gatech.cc.scp.mvtipbtc.model.User;
import edu.gatech.cc.scp.mvtipbtc.repository.UserRepository;
import edu.gatech.cc.scp.mvtipbtc.dto.ProfileUpdateRequest;
import edu.gatech.cc.scp.mvtipbtc.dto.PasswordChangeRequest;
import edu.gatech.cc.scp.mvtipbtc.dto.NotificationSettingsRequest;
import edu.gatech.cc.scp.mvtipbtc.dto.SecuritySettingsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@Service
public class SettingsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Map<String, Object> getUserProfile(User user) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        //profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("firstName", user.getFirstName());
        profile.put("lastName", user.getLastName());
        //profile.put("organization", user.getOrganization());
        //profile.put("role", user.getRole());
        profile.put("createdAt", user.getCreatedAt());
        //profile.put("lastLogin", user.getLastLogin());
        return profile;
    }
    
    public boolean updateUserProfile(User user, ProfileUpdateRequest request) {
        try {
            if (request.getFirstName() != null) {
                user.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null) {
                user.setLastName(request.getLastName());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            /*if (request.getOrganization() != null) {
                user.setOrganization(request.getOrganization());
            }*/
            
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean changePassword(User user, PasswordChangeRequest request) {
        try {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return false;
            }
            
            String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(encodedNewPassword);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public Map<String, Object> getNotificationSettings(User user) {
        Map<String, Object> settings = new HashMap<>();
        
        // Default notification settings
        settings.put("emailAlerts", true);
        settings.put("pushNotifications", true);
        settings.put("criticalAlerts", true);
        settings.put("highRiskAlerts", true);
        settings.put("mediumRiskAlerts", false);
        settings.put("reportGeneration", true);
        settings.put("weeklyDigest", true);
        
        return settings;
    }
    
    public boolean updateNotificationSettings(User user, NotificationSettingsRequest request) {
        try {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public Map<String, Object> getSecuritySettings(User user) {
        Map<String, Object> settings = new HashMap<>();
        
        // Default security settings
        settings.put("twoFactorAuth", false);
        settings.put("sessionTimeout", 30);
        settings.put("passwordExpiry", 90);
        settings.put("loginNotifications", true);
        
        return settings;
    }
    
    public boolean updateSecuritySettings(User user, SecuritySettingsRequest request) {
        try {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String exportUserData(User user) {
        try {
            String exportFileName = "user_data_export_" + user.getId() + "_" + System.currentTimeMillis() + ".json";
            return "https://mvtipbtc-exports.s3.amazonaws.com/" + exportFileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to export user data");
        }
    }
    
    public void clearUserCache(User user) {
    	
    }
    
    public boolean deleteUserAccount(User user) {
        try {
            //user.setActive(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

