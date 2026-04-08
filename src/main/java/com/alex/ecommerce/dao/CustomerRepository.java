package com.alex.ecommerce.dao;

import com.alex.ecommerce.entity.Customer;

import java.util.List;

public interface CustomerRepository extends org.springframework.data.jpa.repository.JpaRepository<com.alex.ecommerce.entity.Customer, Integer> {
    Customer findByEmail(String email);

}
