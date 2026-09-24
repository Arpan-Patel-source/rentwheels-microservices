package com.example.driverservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.driverservice.entity.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {

}
