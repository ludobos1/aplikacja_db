package com.ludobos1.repositories;

import com.ludobos1.encje.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsRepository extends JpaRepository<Payment, Integer> {
}
