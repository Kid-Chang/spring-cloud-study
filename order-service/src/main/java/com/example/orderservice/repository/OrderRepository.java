package com.example.orderservice.repository;

import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<OrderEntity, Long>{
    OrderEntity findByOrderId(String orderId);
    Iterable<OrderEntity> findAllByUserId(String userId);
    }
