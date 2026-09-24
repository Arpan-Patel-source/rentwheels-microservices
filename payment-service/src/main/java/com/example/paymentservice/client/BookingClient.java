package com.example.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.paymentservice.dto.BookingDto;

@FeignClient(name = "booking-service")
public interface BookingClient {

    @GetMapping("/bookings/{id}")
    BookingDto getBookingById(@PathVariable("id") Long id);
}
