package com.udacity.jc.critter.service;

import com.udacity.jc.critter.dataaccess.CustomerRepository;
import com.udacity.jc.critter.domain.Customer;
import com.udacity.jc.critter.domain.Person;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private CustomerRepository customerRepository;
    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }
    public boolean saveCustomer(Customer customer){
        customerRepository.save(customer);
        if (customer.getId() >= 0L){
            return true;
        } else return  false;
    }
    public Iterable<Customer> findAllCustomers(){
        return customerRepository.findAll();
    }
    public Optional<Customer> findCustomerById(Long id){
        return customerRepository.findById(id);
    }
    public Customer findByPetId(Long id){
        return customerRepository.findCustomerByPetIdsIsContaining(id);
    }
}
