package com.example.customerservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.customerservice.entity.Customer;
import com.example.customerservice.exception.BadRequestException;
import com.example.customerservice.exception.DuplicateResourceException;
import com.example.customerservice.exception.ResourceNotFoundException;
import com.example.customerservice.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer addCustomer(Customer customer) {
        if (customer.getId() == null) {
            throw new BadRequestException("Customer id is required");
        }
        if (customerRepository.existsById(customer.getId())) {
            throw new DuplicateResourceException("Customer with id " + customer.getId() + " already exists");
        }
        return customerRepository.save(customer);
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + id + " not found"));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateCustomer(Long id, Customer customer) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer with id " + id + " not found");
        }
        customer.setId(id);
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer with id " + id + " not found");
        }
        customerRepository.deleteById(id);
    }
}
