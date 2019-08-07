package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.account;

public interface AccountRepo extends JpaRepository<account,String>{
	
	@Query(value = "SELECT * FROM account a WHERE a.user_user_id = ?1",nativeQuery = true)
	public account findByUserId(String id);
}
