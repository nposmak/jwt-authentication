package com.nposmak.app.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nposmak.app.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
}
