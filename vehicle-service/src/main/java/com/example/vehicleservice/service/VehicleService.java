package com.example.vehicleservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.vehicleservice.entity.Vehicle;
import com.example.vehicleservice.exception.BadRequestException;
import com.example.vehicleservice.exception.DuplicateResourceException;
import com.example.vehicleservice.exception.ResourceNotFoundException;
import com.example.vehicleservice.repository.VehicleRepository;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        if (vehicle.getId() == null) {
            throw new BadRequestException("Vehicle id is required");
        }
        if (vehicleRepository.existsById(vehicle.getId())) {
            throw new DuplicateResourceException("Vehicle with id " + vehicle.getId() + " already exists");
        }
        return vehicleRepository.save(vehicle);
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with id " + id + " not found"));
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicle) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle with id " + id + " not found");
        }
        vehicle.setId(id);
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle with id " + id + " not found");
        }
        vehicleRepository.deleteById(id);
    }
}
