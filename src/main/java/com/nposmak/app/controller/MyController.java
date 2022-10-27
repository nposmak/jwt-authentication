package com.nposmak.app.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {
	

	@GetMapping("/admin")
	public String homeAdmin(Principal principal) {
		return "Hello mr. " + principal.getName();
	}
	
	@GetMapping("/user")
	public String homeUser(Principal principal) {
		return "Hello mr. " + principal.getName();
	}
}
