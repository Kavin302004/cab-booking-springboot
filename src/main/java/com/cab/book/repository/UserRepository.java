package com.cab.book.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cab.book.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {
	Optional<User> findByEmail(String Email);
	
}