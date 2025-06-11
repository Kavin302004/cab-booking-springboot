// service/AuthService.java
package com.cab.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final DriverService driverService;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authManager,
            DriverService driverService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.driverService = driverService;
    }

    public Boolean validation(String password) {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-])[A-Za-z\\d!@#$%^&*()_+-=]{8,}$";
        return password.matches(regex);
    }

    @Transactional
    public String register(RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        if (validation(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        } else {
            return "Password constraints not met";
        }

        Role role = Role.valueOf(request.getRole());
        user.setRole(role);

        // Save the user first
        user = userRepository.save(user);

        // If the user is a driver, create a Driver entity
        if (role == Role.ROLE_DRIVER) {
            driverService.createDriver(user);
        }

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
