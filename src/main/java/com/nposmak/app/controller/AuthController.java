package com.nposmak.app.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nposmak.app.security.TokenService;
import com.nposmak.app.security.usr_details.CustomUsrDetails;
import com.nposmak.app.security.usr_details.CustomUsrDetailsService;

@RestController
public class AuthController {
	
	private final TokenService tokenService;
	private final AuthenticationManager authManager;
	private final CustomUsrDetailsService usrDetailsService;
	
	
	public AuthController(TokenService tokenService, AuthenticationManager authManager,
			CustomUsrDetailsService usrDetailsService) {
		super();
		this.tokenService = tokenService;
		this.authManager = authManager;
		this.usrDetailsService = usrDetailsService;
	}


	record LoginRequest(String username, String password) {};
	record LoginResponse(String message, String access_jwt_token, String refresh_jwt_token) {};
	@PostMapping("/login")
	public LoginResponse login(@RequestBody LoginRequest request) {
		
		UsernamePasswordAuthenticationToken authenticationToken = 
				new UsernamePasswordAuthenticationToken(request.username, request.password);
		Authentication auth = authManager.authenticate(authenticationToken);
		
		CustomUsrDetails user = (CustomUsrDetails) usrDetailsService.loadUserByUsername(request.username);
		String access_token = tokenService.generateAccessToken(user);
		String refresh_token = tokenService.generateRefreshToken(user);
		
		return new LoginResponse("User with email = "+ request.username + " successfully logined!"
				
				, access_token, refresh_token);
	}
	
	
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
