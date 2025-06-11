package com.cab.book.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cab_availability")
public class CabAvailability {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cab_id", nullable = false)
    private Cab cab;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CabStatus status;
    
    public CabAvailability() {}
    
    public CabAvailability(Cab cab, LocalDate date, CabStatus status) {
        this.cab = cab;
        this.date = date;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Cab getCab() {
        return cab;
    }
    
    public void setCab(Cab cab) {
        this.cab = cab;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public CabStatus getStatus() {
        return status;
    }
    
    public void setStatus(CabStatus status) {
        this.status = status;
    }
} 