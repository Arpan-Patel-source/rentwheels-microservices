package com.example.paymentservice.service;

import java.util.List;

import feign.FeignException;
import org.springframework.stereotype.Service;

import com.example.paymentservice.client.CustomerClient;
import com.example.paymentservice.client.BookingClient;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.exception.BadRequestException;
import com.example.paymentservice.exception.DuplicateResourceException;
import com.example.paymentservice.exception.ResourceNotFoundException;
import com.example.paymentservice.exception.ServiceUnavailableException;
import com.example.paymentservice.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerClient customerClient;
    private final BookingClient bookingClient;

    public PaymentService(PaymentRepository paymentRepository, CustomerClient customerClient, BookingClient bookingClient) {
        this.paymentRepository = paymentRepository;
        this.customerClient = customerClient;
        this.bookingClient = bookingClient;
    }

    public Payment addPayment(Payment payment) {
        if (payment.getId() == null) {
            throw new BadRequestException("Payment id is required");
        }
        if (paymentRepository.existsById(payment.getId())) {
            throw new DuplicateResourceException("Payment with id " + payment.getId() + " already exists");
        }
        verifyCustomerExists(payment.getCustomerId());
        verifyBookingExists(payment.getBookingId());
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment with id " + id + " not found"));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment updatePayment(Long id, Payment payment) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment with id " + id + " not found");
        }
        verifyCustomerExists(payment.getCustomerId());
        verifyBookingExists(payment.getBookingId());
        payment.setId(id);
        return paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment with id " + id + " not found");
        }
        paymentRepository.deleteById(id);
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
