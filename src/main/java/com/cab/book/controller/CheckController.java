package com.cab.book.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckController {
	@GetMapping("/user")
	public String index() {
		return "User accepted";
	}
}