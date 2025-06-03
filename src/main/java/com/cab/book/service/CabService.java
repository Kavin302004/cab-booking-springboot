package com.cab.book.service;

import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cab.book.entity.Cab;
import com.cab.book.entity.CabStatus;
import com.cab.book.entity.Driver;
import com.cab.book.repository.CabRepository;
import com.cab.book.repository.DriverRepository;

@Service
public class CabService {
	
	@Autowired
	private CabRepository cabRepository;
	
	@Autowired
	private DriverRepository driverRepository; 
	
	public List<Cab> getAllCabs() {
		return cabRepository.findAll();
	}
	public List<Cab> getAvailableCabs() {
		return cabRepository.findByStatus(CabStatus.AVAILABLE);
	}
	
	public Driver assignCabtoDriver(Long driverId,Long cabId) {
		Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new RuntimeException("Driver not found"));
		
		Cab cab = cabRepository.findById(cabId).orElseThrow(()-> new RuntimeException("Cab not found"));
		
		cab.setStatus(CabStatus.AVAILABLE);
		driver.setCab(cab);
		cab.setDriver(driver);
		
		cabRepository.save(cab);
		
		return driverRepository.save(driver);
		
	}
	
}