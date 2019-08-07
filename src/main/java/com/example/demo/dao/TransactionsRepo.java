package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Transactions;

public interface TransactionsRepo extends JpaRepository<Transactions, Integer>{

}
