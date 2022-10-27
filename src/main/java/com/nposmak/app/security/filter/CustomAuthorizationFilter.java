package com.nposmak.app.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nposmak.app.security.TokenService;
import com.nposmak.app.security.usr_details.CustomUsrDetails;
import com.nposmak.app.security.usr_details.CustomUsrDetailsService;


public class CustomAuthorizationFilter extends OncePerRequestFilter{
	
	private final TokenService tokenService;
	private final CustomUsrDetailsService customUsrDetailsService;
	
	public CustomAuthorizationFilter(TokenService tokenService, CustomUsrDetailsService customUsrDetailsService) {
		super();
		this.tokenService = tokenService;
		this.customUsrDetailsService = customUsrDetailsService;
	}

	
	

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
        if(request.getServletPath().equals("/login") || request.getServletPath().equals("/token/refresh")) {
            filterChain.doFilter(request, response);
    	} else {
    		String authorizationHeader = request.getHeader("Authorization");
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            	
            	String token = authorizationHeader.substring(7, authorizationHeader.length());
            	String email = tokenService.parseToken(token);
            	CustomUsrDetails user = (CustomUsrDetails) customUsrDetailsService.loadUserByUsername(email);
            	UsernamePasswordAuthenticationToken authenticationToken = 
            			new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
            	
            	SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            	
            } else {
            	filterChain.doFilter(request, response);
            }
    	}

	
	
	
	}


}
