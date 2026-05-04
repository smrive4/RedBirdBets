package com.predictionmarket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.predictionmarket.model.User;
// Repository interface for performing CRUD operations on User entities and custom queries for user data such as finding by username and ordering by balance
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    @Query("SELECT u FROM User u ORDER BY u.balance DESC")
    List<User> findAllOrderByBalanceDesc();

}