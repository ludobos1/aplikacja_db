package com.ludobos1.repositories;

import com.ludobos1.encje.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Integer> {
  Order findById(int id);
}
