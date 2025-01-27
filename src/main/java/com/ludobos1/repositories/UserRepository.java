package com.ludobos1.repositories;

import com.ludobos1.encje.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsername(String username);
  User findByEmail(String email);
}