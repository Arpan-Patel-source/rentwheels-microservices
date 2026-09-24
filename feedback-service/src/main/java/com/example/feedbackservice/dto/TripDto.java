package com.example.feedbackservice.dto;

import java.time.LocalDate;

public record TripDto(Long id, String title, String routeDetails, Long vehicleId, Long driverId, LocalDate dueDate) {

}
