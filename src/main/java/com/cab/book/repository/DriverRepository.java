package com.cab.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cab.book.entity.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {
	
}