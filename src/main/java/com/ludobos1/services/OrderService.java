package com.ludobos1.services;

import com.ludobos1.encje.Order;
import com.ludobos1.repositories.OrdersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
  @Autowired
  private OrdersRepository ordersRepository;
  public void createOrder(Order order) {
    ordersRepository.save(order);
  }
  @Transactional
  public void updateOrder(Order order) {
    ordersRepository.save(order);
  }
  public Order findOrderById(int id) {
    return ordersRepository.findById(id);
  }
}
