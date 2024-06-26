package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.model.Delivery;

@Repository
public interface DeliveryRepo extends JpaRepository<Delivery, Integer> {

}
