package com.cab.book.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cab.book.entity.Cab;
import com.cab.book.entity.CabStatus;
import com.cab.book.entity.Driver;
import com.cab.book.entity.CabAvailability;
import com.cab.book.repository.CabRepository;
import com.cab.book.repository.DriverRepository;
import com.cab.book.repository.CabAvailabilityRepository;

@Service
public class CabService {
	
	@Autowired
	private CabRepository cabRepository;
	
	@Autowired
	private DriverRepository driverRepository; 
	
	@Autowired
	private CabAvailabilityRepository cabAvailabilityRepository;
	
	public List<Cab> getAllCabs() {
		return cabRepository.findAll();
	}
	
	public List<Cab> getAvailableCabsForDate(LocalDate date) {
		List<CabAvailability> bookedCabs = cabAvailabilityRepository.findByDateAndStatus(date, CabStatus.BOOKED);
		List<Long> bookedCabIds = bookedCabs.stream()
			.map(ca -> ca.getCab().getId())
			.collect(Collectors.toList());
		
		return cabRepository.findAll().stream()
			.filter(cab -> !bookedCabIds.contains(cab.getId()))
			.collect(Collectors.toList());
	}
	
	
	
	public Driver assignCabtoDriver(Long driverId, Long cabId) {
		Driver driver = driverRepository.findById(driverId)
			.orElseThrow(() -> new RuntimeException("Driver not found"));
		
		Cab cab = cabRepository.findById(cabId)
			.orElseThrow(() -> new RuntimeException("Cab not found"));
		
		// Set default availability for today
		CabAvailability todayAvailability = new CabAvailability(cab, LocalDate.now(), CabStatus.AVAILABLE);
		cabAvailabilityRepository.save(todayAvailability);
		
		driver.setCab(cab);
		cab.setDriver(driver);
		
		cabRepository.save(cab);
		return driverRepository.save(driver);
	}
	
	public boolean isCabAvailableForDate(Cab cab, LocalDate date) {
		return cabAvailabilityRepository.findByCabAndDate(cab, date)
			.map(availability -> availability.getStatus() == CabStatus.AVAILABLE)
			.orElse(true); // If no availability record exists, cab is considered available
	}
}