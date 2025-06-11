package com.cab.book.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cab.book.entity.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {
	Optional<Driver> findByEmail(String email);
	Optional<Driver> findByUserId(Long userId);
}