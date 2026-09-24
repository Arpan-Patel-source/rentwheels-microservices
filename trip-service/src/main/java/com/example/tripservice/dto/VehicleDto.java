package com.example.tripservice.dto;

import java.math.BigDecimal;

public record VehicleDto(Long id, String vehicleName, String model, String type, BigDecimal dailyFee) {

}
