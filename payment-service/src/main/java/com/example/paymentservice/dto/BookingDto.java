package com.example.paymentservice.dto;

import java.time.LocalDate;

public record BookingDto(Long id, Long customerId, Long vehicleId, LocalDate bookingDate, Integer durationDays, String status) {

}
