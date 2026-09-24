package com.example.driverservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.driverservice.entity.Driver;
import com.example.driverservice.exception.BadRequestException;
import com.example.driverservice.exception.DuplicateResourceException;
import com.example.driverservice.exception.ResourceNotFoundException;
import com.example.driverservice.repository.DriverRepository;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public Driver addDriver(Driver driver) {
        if (driver.getId() == null) {
            throw new BadRequestException("Driver id is required");
        }
        if (driverRepository.existsById(driver.getId())) {
            throw new DuplicateResourceException("Driver with id " + driver.getId() + " already exists");
        }
        return driverRepository.save(driver);
    }

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver with id " + id + " not found"));
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver updateDriver(Long id, Driver driver) {
        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Driver with id " + id + " not found");
        }
        driver.setId(id);
        return driverRepository.save(driver);
    }

    public void deleteDriver(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new ResourceNotFoundException("Driver with id " + id + " not found");
        }
        driverRepository.deleteById(id);
    }
}
