package com.ludobos1.repositories;

import com.ludobos1.encje.Order_items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Order_itemsRepository extends JpaRepository<Order_items, Integer> {
  List<Order_items> findByOrder_id(int order_id);
  @Procedure
  void addBookToOrder(int orderId, int bookId);
}
