package com.example.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.bookingservice.dto.CustomerDto;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/customers/{id}")
    CustomerDto getCustomerById(@PathVariable("id") Long id);
}
