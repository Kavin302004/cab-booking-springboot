// service/AuthService.java
package com.cab.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cab.book.dto.LoginRequest;
import com.cab.book.dto.RegisterRequest;
import com.cab.book.entity.Role;
import com.cab.book.entity.User;
import com.cab.book.repository.UserRepository;
import com.cab.book.security.JwtService;

@Service

public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authManager) {
this.userRepository = userRepository;
this.passwordEncoder = passwordEncoder;
this.jwtService = jwtService;
this.authManager = authManager;
}

    public String register(RegisterRequest request) {
    	User user = new User();
    	user.setName(request.getName());
    	user.setEmail(request.getEmail());
    	user.setPassword(passwordEncoder.encode(request.getPassword()));
    	user.setRole(Role.valueOf(request.getRole()));


        userRepository.save(user);
        return jwtService.generateToken(user.getEmail());
    }

    public String login(LoginRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow();
        return jwtService.generateToken(user.getEmail());
    }
}
