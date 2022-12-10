package com.udacity.jc.critter.dataaccess;

import com.udacity.jc.critter.domain.Customer;
import com.udacity.jc.critter.domain.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    public Customer findCustomerByPetIdsIsContaining(Long id);
}
