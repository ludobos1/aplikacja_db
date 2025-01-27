package com.ludobos1.repositories;

import com.ludobos1.encje.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewsRepository extends JpaRepository<Review, Integer> {
}
