package com.example.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.bookingservice.dto.VehicleDto;

@FeignClient(name = "vehicle-service")
public interface VehicleClient {

    @GetMapping("/vehicles/{id}")
    VehicleDto getVehicleById(@PathVariable("id") Long id);
}
