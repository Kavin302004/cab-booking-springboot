package com.cab.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cab.book.entity.Driver;
import com.cab.book.entity.User;
import com.cab.book.repository.DriverRepository;

@Service
public class DriverService {
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Transactional
    public Driver createDriver(User user) {
        // Create and save new driver
        Driver driver = new Driver();
        driver.setName(user.getName());
        driver.setEmail(user.getEmail());
        driver.setUser(user);
        
        return driverRepository.save(driver);
    }
    
    public Driver findByEmail(String email) {
        return driverRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Driver not found with email: " + email));
    }
    
    public Driver findByUserId(Long userId) {
        return driverRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Driver not found for user ID: " + userId));
    }
} 