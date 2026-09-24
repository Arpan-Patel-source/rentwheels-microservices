package com.example.bookingservice.service;

import java.util.List;

import feign.FeignException;
import org.springframework.stereotype.Service;

import com.example.bookingservice.client.CustomerClient;
import com.example.bookingservice.client.VehicleClient;
import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.exception.BadRequestException;
import com.example.bookingservice.exception.DuplicateResourceException;
import com.example.bookingservice.exception.ResourceNotFoundException;
import com.example.bookingservice.exception.ServiceUnavailableException;
import com.example.bookingservice.repository.BookingRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerClient customerClient;
    private final VehicleClient vehicleClient;

    public BookingService(BookingRepository bookingRepository, CustomerClient customerClient, VehicleClient vehicleClient) {
        this.bookingRepository = bookingRepository;
        this.customerClient = customerClient;
        this.vehicleClient = vehicleClient;
    }

    public Booking addBooking(Booking booking) {
        if (booking.getId() == null) {
            throw new BadRequestException("Booking id is required");
        }
        if (bookingRepository.existsById(booking.getId())) {
            throw new DuplicateResourceException("Booking with id " + booking.getId() + " already exists");
        }
        verifyCustomerExists(booking.getCustomerId());
        verifyVehicleExists(booking.getVehicleId());
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with id " + id + " not found"));
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking updateBooking(Long id, Booking booking) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Booking with id " + id + " not found");
        }
        verifyCustomerExists(booking.getCustomerId());
        verifyVehicleExists(booking.getVehicleId());
        booking.setId(id);
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Booking with id " + id + " not found");
        }
        bookingRepository.deleteById(id);
    }

    private void verifyCustomerExists(Long customerId) {
        if (customerId == null) {
            throw new BadRequestException("customerId is required");
        }
        try {
            customerClient.getCustomerById(customerId);
        } catch (FeignException.NotFound e) {
            throw new BadRequestException("Customer with id " + customerId + " does not exist");
        } catch (FeignException e) {
            throw new ServiceUnavailableException("customer-service is unavailable, unable to verify customer with id " + customerId);
        }
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
}
