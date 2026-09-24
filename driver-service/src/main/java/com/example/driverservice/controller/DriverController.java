package com.example.driverservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.driverservice.entity.Driver;
import com.example.driverservice.service.DriverService;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<Driver> addDriver(@RequestBody Driver driver) {
        Driver saved = driverService.addDriver(driver);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public Driver getDriverById(@PathVariable("id") Long id) {
        return driverService.getDriverById(id);
    }

    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @PutMapping("/{id}")
    public Driver updateDriver(@PathVariable("id") Long id, @RequestBody Driver driver) {
        return driverService.updateDriver(id, driver);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable("id") Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }
}
