package com.cab.book.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "time_slots")
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalTime slotTime;  // e.g., "08:00", "10:30"

    @Column(nullable = false)
    private boolean isBooked = false;

    // Default constructor
    public TimeSlot() {}

    // Parameterized constructor
    public TimeSlot(LocalTime slotTime) {
        this.slotTime = slotTime;
        this.isBooked = false;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(LocalTime slotTime) {
        this.slotTime = slotTime;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }
}
