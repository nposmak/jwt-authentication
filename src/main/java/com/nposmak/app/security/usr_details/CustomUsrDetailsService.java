package com.nposmak.app.security.usr_details;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nposmak.app.entity.User;
import com.nposmak.app.repo.UserRepository;



@Service
public class CustomUsrDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepo.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User with email = "+email+" not exist!"));
		return new CustomUsrDetails(user);
	}
}
