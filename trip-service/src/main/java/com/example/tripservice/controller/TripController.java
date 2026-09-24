package com.example.tripservice.controller;

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

import com.example.tripservice.entity.Trip;
import com.example.tripservice.service.TripService;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<Trip> addTrip(@RequestBody Trip trip) {
        Trip saved = tripService.addTrip(trip);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public Trip getTripById(@PathVariable("id") Long id) {
        return tripService.getTripById(id);
    }

    @GetMapping
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    @PutMapping("/{id}")
    public Trip updateTrip(@PathVariable("id") Long id, @RequestBody Trip trip) {
        return tripService.updateTrip(id, trip);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable("id") Long id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }
}
