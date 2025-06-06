package com.cab.book.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cabs")
public class Cab {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false)
	private String registrationNumber;
	
	@Enumerated(EnumType.STRING)
	private CabStatus status;
	
	@OneToOne(mappedBy = "cab")
	@JsonIgnore
	private Driver driver;
	
	// Constructors
    public Cab() {}

    public Cab(String registrationNumber, CabStatus status) {
        this.registrationNumber = registrationNumber;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public CabStatus getStatus() {
        return status;
    }

    public void setStatus(CabStatus status) {
        this.status = status;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}