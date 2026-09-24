package com.example.tripservice.service;

import java.util.List;

import feign.FeignException;
import org.springframework.stereotype.Service;

import com.example.tripservice.client.VehicleClient;
import com.example.tripservice.client.DriverClient;
import com.example.tripservice.entity.Trip;
import com.example.tripservice.exception.BadRequestException;
import com.example.tripservice.exception.DuplicateResourceException;
import com.example.tripservice.exception.ResourceNotFoundException;
import com.example.tripservice.exception.ServiceUnavailableException;
import com.example.tripservice.repository.TripRepository;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final VehicleClient vehicleClient;
    private final DriverClient driverClient;

    public TripService(TripRepository tripRepository, VehicleClient vehicleClient, DriverClient driverClient) {
        this.tripRepository = tripRepository;
        this.vehicleClient = vehicleClient;
        this.driverClient = driverClient;
    }

    public Trip addTrip(Trip trip) {
        if (trip.getId() == null) {
            throw new BadRequestException("Trip id is required");
        }
        if (tripRepository.existsById(trip.getId())) {
            throw new DuplicateResourceException("Trip with id " + trip.getId() + " already exists");
        }
        verifyVehicleExists(trip.getVehicleId());
        verifyDriverExists(trip.getDriverId());
        return tripRepository.save(trip);
    }

    public Trip getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + id + " not found"));
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Trip updateTrip(Long id, Trip trip) {
        if (!tripRepository.existsById(id)) {
            throw new ResourceNotFoundException("Trip with id " + id + " not found");
        }
        verifyVehicleExists(trip.getVehicleId());
        verifyDriverExists(trip.getDriverId());
        trip.setId(id);
        return tripRepository.save(trip);
    }

    public void deleteTrip(Long id) {
        if (!tripRepository.existsById(id)) {
            throw new ResourceNotFoundException("Trip with id " + id + " not found");
        }
        tripRepository.deleteById(id);
    }

    private void verifyVehicleExists(Long vehicleId) {
        if (vehicleId == null) {
            throw new BadRequestException("vehicleId is required");
        }
        try {
            vehicleClient.getVehicleById(vehicleId);
        } catch (FeignException.NotFound e) {
            throw new BadRequestException("Vehicle with id " + vehicleId + " does not exist");
        } catch (FeignException e) {
            throw new ServiceUnavailableException("vehicle-service is unavailable, unable to verify vehicle with id " + vehicleId);
        }
    }

    private void verifyDriverExists(Long driverId) {
        if (driverId == null) {
            throw new BadRequestException("driverId is required");
        }
        try {
            driverClient.getDriverById(driverId);
        } catch (FeignException.NotFound e) {
            throw new BadRequestException("Driver with id " + driverId + " does not exist");
        } catch (FeignException e) {
            throw new ServiceUnavailableException("driver-service is unavailable, unable to verify driver with id " + driverId);
        }
    }
}
