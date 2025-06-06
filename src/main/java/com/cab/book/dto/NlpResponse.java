package com.cab.book.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public class NlpResponse {
    private String pickupLocation;
    private String dropLocation;
    
    
    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    @JsonProperty("LocalTime")
    private LocalTime LocalTime;
    
    @JsonProperty("LocalDate")
    private LocalDate LocalDate;

    public NlpResponse() {
    }

    public NlpResponse(String pickupLocation, String dropLocation, LocalTime LocalTime, LocalDate LocalDate) {
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.LocalTime = LocalTime;
        this.LocalDate = LocalDate;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public LocalTime getLocalTime() {
        return  LocalTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.LocalTime = localTime;
        
    }

    public LocalDate getLocalDate() {
        return LocalDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.LocalDate = localDate;
    }
    
    @Override
    public String toString() {
        return "NlpResponse{" +
            "pickupLocation='" + pickupLocation + '\'' +
            ", dropLocation='" + dropLocation + '\'' +
            ", localDate=" + LocalDate +
            ", localTime=" + LocalTime +
            '}';
    }
}
