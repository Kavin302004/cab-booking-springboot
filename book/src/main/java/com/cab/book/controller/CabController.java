package com.cab.book.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cab.book.entity.Cab;
import com.cab.book.service.CabService;

@RestController
@RequestMapping("api/cabs")
public class CabController {
	
	@Autowired
	private CabService cabService;
	
	@GetMapping
	public List<Cab> getAllCabs() {
		return cabService.getAllCabs();
	}
	
	
	
	@PostMapping("/assign")
	public String assignCabtoDriver(
			@RequestParam Long DriverId,
			@RequestParam Long CabId
			) 
	{
		cabService.assignCabtoDriver(DriverId, CabId);
		return "Cab Assigned to Driver successfully";
	}
	
}