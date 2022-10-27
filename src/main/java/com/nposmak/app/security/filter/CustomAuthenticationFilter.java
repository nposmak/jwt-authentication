package com.nposmak.app.security.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nposmak.app.security.TokenService;
import com.nposmak.app.security.usr_details.CustomUsrDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private final TokenService tokenService;
	private final DaoAuthenticationProvider daoAuthenticationManager;
	
	public CustomAuthenticationFilter(DaoAuthenticationProvider daoAuthenticationManager,TokenService tokenService) {
		super();
		this.tokenService = tokenService;
		this.daoAuthenticationManager = daoAuthenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		try {
			String body = request.getReader().lines().collect(Collectors.joining());
			Map<String, String> requestBody = parseBody(body);
			String email = requestBody.get("username");
			String password = requestBody.get("password");
			log.info("User with email = {} trying to login!", email);
			UsernamePasswordAuthenticationToken authenticationToken = 
					new UsernamePasswordAuthenticationToken(email, password);
			
			return daoAuthenticationManager.authenticate(authenticationToken);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
		
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		CustomUsrDetails user = (CustomUsrDetails) authResult.getPrincipal();

        String access_token = tokenService.generateAccessToken(user);

        String refresh_token = tokenService.generateRefreshToken(user);
        
        Map<String, String> tokens = new TreeMap<>();
        tokens.put("access_jwt_token", access_token);
        tokens.put("refresh_jwt_token", refresh_token);
        
        response.setContentType("application/json");
        
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        
	}
	

	private Map<String, String> parseBody(String jsonRespBody) {
    	Map<String, String> userCredentials = new HashMap<>();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonRespBody.trim());
            JsonNode username = jsonNode.get("username");
            JsonNode password = jsonNode.get("password");
            if (username != null) 
            	userCredentials.put("username", username.asText());
            if(password != null)
            	userCredentials.put("password",password.asText());
            return userCredentials;
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

	
}
