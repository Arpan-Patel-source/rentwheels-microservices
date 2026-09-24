package com.example.feedbackservice.service;

import java.util.List;

import feign.FeignException;
import org.springframework.stereotype.Service;

import com.example.feedbackservice.client.CustomerClient;
import com.example.feedbackservice.client.TripClient;
import com.example.feedbackservice.client.BookingClient;
import com.example.feedbackservice.entity.Feedback;
import com.example.feedbackservice.exception.BadRequestException;
import com.example.feedbackservice.exception.DuplicateResourceException;
import com.example.feedbackservice.exception.ResourceNotFoundException;
import com.example.feedbackservice.exception.ServiceUnavailableException;
import com.example.feedbackservice.repository.FeedbackRepository;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final CustomerClient customerClient;
    private final TripClient tripClient;
    private final BookingClient bookingClient;

    public FeedbackService(FeedbackRepository feedbackRepository, CustomerClient customerClient, TripClient tripClient, BookingClient bookingClient) {
        this.feedbackRepository = feedbackRepository;
        this.customerClient = customerClient;
        this.tripClient = tripClient;
        this.bookingClient = bookingClient;
    }

    public Feedback addFeedback(Feedback feedback) {
        if (feedback.getId() == null) {
            throw new BadRequestException("Feedback id is required");
        }
        if (feedbackRepository.existsById(feedback.getId())) {
            throw new DuplicateResourceException("Feedback with id " + feedback.getId() + " already exists");
        }
        verifyCustomerExists(feedback.getCustomerId());
        verifyTripExists(feedback.getTripId());
        verifyBookingExists(feedback.getBookingId());
        return feedbackRepository.save(feedback);
    }

    public Feedback getFeedbackById(Long id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback with id " + id + " not found"));
    }

    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    public Feedback updateFeedback(Long id, Feedback feedback) {
        if (!feedbackRepository.existsById(id)) {
            throw new ResourceNotFoundException("Feedback with id " + id + " not found");
        }
        verifyCustomerExists(feedback.getCustomerId());
        verifyTripExists(feedback.getTripId());
        verifyBookingExists(feedback.getBookingId());
        feedback.setId(id);
        return feedbackRepository.save(feedback);
    }

    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new ResourceNotFoundException("Feedback with id " + id + " not found");
        }
        feedbackRepository.deleteById(id);
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

    private void verifyTripExists(Long tripId) {
        if (tripId == null) {
            throw new BadRequestException("tripId is required");
        }
        try {
            tripClient.getTripById(tripId);
        } catch (FeignException.NotFound e) {
            throw new BadRequestException("Trip with id " + tripId + " does not exist");
        } catch (FeignException e) {
            throw new ServiceUnavailableException("trip-service is unavailable, unable to verify trip with id " + tripId);
        }
    }

    private void verifyBookingExists(Long bookingId) {
        if (bookingId == null) {
            throw new BadRequestException("bookingId is required");
        }
        try {
            bookingClient.getBookingById(bookingId);
        } catch (FeignException.NotFound e) {
            throw new BadRequestException("Booking with id " + bookingId + " does not exist");
        } catch (FeignException e) {
            throw new ServiceUnavailableException("booking-service is unavailable, unable to verify booking with id " + bookingId);
        }
    }
}
