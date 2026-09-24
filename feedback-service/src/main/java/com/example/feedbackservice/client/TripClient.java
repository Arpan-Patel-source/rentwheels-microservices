package com.example.feedbackservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.feedbackservice.dto.TripDto;

@FeignClient(name = "trip-service")
public interface TripClient {

    @GetMapping("/trips/{id}")
    TripDto getTripById(@PathVariable("id") Long id);
}
