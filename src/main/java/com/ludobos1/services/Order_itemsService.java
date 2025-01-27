package com.ludobos1.services;

import com.ludobos1.encje.Book;
import com.ludobos1.encje.Order;
import com.ludobos1.encje.Order_items;
import com.ludobos1.repositories.Order_itemsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Order_itemsService {
  @Autowired
  private Order_itemsRepository order_itemsRepository;
  @Autowired
  private EntityManager entityManager;

  public void addItem(Order order, Book book) {
    order_itemsRepository.addBookToOrder(order.getId(), book.getId());
  }
  public List<Order_items> findByOrderId(int id) {
    return order_itemsRepository.findByOrder_id(id);
  }
}
