package com.nposmak.app.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nposmak.app.security.TokenService;
import com.nposmak.app.security.usr_details.CustomUsrDetails;
import com.nposmak.app.security.usr_details.CustomUsrDetailsService;

@RestController
public class AuthController {
	
	@Autowired
	private CustomUsrDetailsService usrDetailsService;
	
	@Autowired
	private TokenService tokenService;
	

	
	
	record RefreshTokenResponse(String access_jwt_token, String refresh_jwt_token) {};
	@GetMapping("/token/refresh")
	public RefreshTokenResponse refreshToken(HttpServletRequest request) {
		 String headerAuth = request.getHeader("Authorization");		 
		 String refreshToken = headerAuth.substring(7, headerAuth.length());
		
		String email = tokenService.parseToken(refreshToken);
		CustomUsrDetails user = (CustomUsrDetails) usrDetailsService.loadUserByUsername(email);
		String access_token = tokenService.generateAccessToken(user);
		String refresh_token = tokenService.generateRefreshToken(user);
		
		return new RefreshTokenResponse(access_token, refresh_token);
		
	}
	
}
