package com.smartshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartshop.model.entity.User;
import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findByUsername(String username);
}
