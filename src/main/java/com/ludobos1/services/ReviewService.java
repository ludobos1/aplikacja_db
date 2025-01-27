package com.ludobos1.services;

import com.ludobos1.encje.Review;
import com.ludobos1.repositories.ReviewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class ReviewService {
  @Autowired
  private ReviewsRepository ReviewsRepository;
  public String save(Review review) {
    try {
      ReviewsRepository.save(review);
      return "Review saved successfully";
    }catch (JpaSystemException e){
      return "error - user already added a review to this book";
    }
  }
}
