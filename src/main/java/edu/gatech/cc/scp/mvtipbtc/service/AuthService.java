package edu.gatech.cc.scp.mvtipbtc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.gatech.cc.scp.mvtipbtc.config.JwtUtil;
import edu.gatech.cc.scp.mvtipbtc.dto.LoginRequest;
import edu.gatech.cc.scp.mvtipbtc.dto.LoginResponse;
import edu.gatech.cc.scp.mvtipbtc.dto.RegisterRequest;
import edu.gatech.cc.scp.mvtipbtc.dto.UserDto;
import edu.gatech.cc.scp.mvtipbtc.model.User;
import edu.gatech.cc.scp.mvtipbtc.repository.UserRepository;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        System.out.println("Good Password");
        String token = jwtUtil.generateToken(user.getEmail());
        System.out.println(token);
        UserDto userDto = new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
        
        return new LoginResponse(token, userDto);
    }
    
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getFirstName(), request.getLastName(), request.getEmail(), encodedPassword);
        
        userRepository.save(user);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

