package com.cab.book.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class NlpResponse {
    private String pickupLocation;
    private String dropLocation;
    private LocalTime localTime;
    private LocalDate localDate;

    public NlpResponse() {
    }

    public NlpResponse(String pickupLocation, String dropLocation, LocalTime localTime, LocalDate localDate) {
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.localTime = localTime;
        this.localDate = localDate;
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
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }
}
