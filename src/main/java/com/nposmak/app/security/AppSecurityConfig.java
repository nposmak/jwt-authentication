package com.nposmak.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nposmak.app.security.filter.CustomAuthenticationFilter;
import com.nposmak.app.security.filter.CustomAuthorizationFilter;
import com.nposmak.app.security.usr_details.CustomUsrDetailsService;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class AppSecurityConfig {
	
	private final RsaProperties rsaKeys;
	
	public AppSecurityConfig(RsaProperties rsaKeys) {
		this.rsaKeys = rsaKeys;
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public UserDetailsService userDetailsService() {
        return new CustomUsrDetailsService();
    }
	
	@Bean
	public AuthenticationManager authenticationManager(
	        AuthenticationConfiguration authConfig) throws Exception {
	    return authConfig.getAuthenticationManager();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(userDetailsService());
	    authProvider.setPasswordEncoder(passwordEncoder());
	    return authProvider;
	}
	
	
	@Bean
	JwtEncoder jwtEncoder() {
		JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
		JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwkSource);
	}
	
	@Bean
	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
	}
	
	@Bean
	TokenService tokenService() {
		return new TokenService(jwtEncoder());
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		return http
						.csrf(csrf -> csrf.disable())
						.authorizeRequests(auth -> auth
							.mvcMatchers("/login").permitAll()
							.mvcMatchers("/token/refresh").permitAll()
							.mvcMatchers("/admin").hasAuthority("SCOPE_adm")
							.mvcMatchers("/user").hasAuthority("SCOPE_usr")
							.anyRequest().authenticated())
						.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
						.oauth2ResourceServer(resourseServer -> resourseServer.jwt() )
							.addFilter(new CustomAuthenticationFilter(authenticationProvider(),  tokenService()))
							.addFilterBefore(new CustomAuthorizationFilter(tokenService(), 
									(CustomUsrDetailsService) userDetailsService()), UsernamePasswordAuthenticationFilter.class)
							.build();
	}

}
