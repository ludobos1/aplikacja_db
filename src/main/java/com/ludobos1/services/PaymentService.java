package com.ludobos1.services;

import com.ludobos1.encje.Order;
import com.ludobos1.encje.Payment;
import com.ludobos1.repositories.PaymentsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
  @Autowired
  private PaymentsRepository paymentsRepository;

  public Payment createPayment(Payment payment) {
    return paymentsRepository.save(payment);
  }
  @Transactional
  public void updatePayment(Payment payment) {
    paymentsRepository.save(payment);
  }

}
