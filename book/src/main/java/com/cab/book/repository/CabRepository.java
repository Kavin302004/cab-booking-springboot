package com.cab.book.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cab.book.entity.Cab;
import com.cab.book.entity.CabStatus;

public interface CabRepository extends JpaRepository<Cab, Long> {
	List<Cab> findByStatus(CabStatus status);
	
}