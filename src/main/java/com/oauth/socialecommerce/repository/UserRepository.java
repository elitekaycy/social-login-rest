package com.oauth.socialecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oauth.socialecommerce.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByName(String username);

  Optional<User> findByEmail(String email);

}
