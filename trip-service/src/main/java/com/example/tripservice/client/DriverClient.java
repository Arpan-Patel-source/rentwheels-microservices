package com.example.tripservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.tripservice.dto.DriverDto;

@FeignClient(name = "driver-service")
public interface DriverClient {

    @GetMapping("/drivers/{id}")
    DriverDto getDriverById(@PathVariable("id") Long id);
}
