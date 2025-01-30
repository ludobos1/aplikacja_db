package com.ludobos1.services;

import com.ludobos1.DataSourceContextHolder;
import com.ludobos1.encje.User;
import com.ludobos1.repositories.UserRepository;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  private static Validator validator;

  static {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  public String registerUser(User user) {
    if (userRepository.findByUsername(user.getUsername()) != null) {
      return "username already in use";
    } else if(userRepository.findByEmail(user.getEmail()) != null){
      return "email already in use";
    }
    try {
      validateUser(user);
    } catch (ConstraintViolationException e) {
      return e.getMessage();
    }

    userRepository.save(user);
    return "account registered successfully";
  }

  public User authenticateUser(String username, String password) {
    User user = userRepository.findByUsername(username);
    if (user != null && user.passwordEncoder.matches(password, user.getPassword())) {
      return user;
    }
    return null;
  }
  public void validateUser(User user) {
    Set<ConstraintViolation<User>> violations = validator.validate(user);
    if (!violations.isEmpty()) {
      for (ConstraintViolation<User> violation : violations) {
        System.out.println("Błąd walidacji: " + violation.getMessage());
      }
      throw new ConstraintViolationException(violations);
    }
  }
  public void loginAsAdmin() {
    DataSourceContextHolder.setCurrentDb("db1");
  }

  public void loginAsUser() {
    DataSourceContextHolder.setCurrentDb("db2");
  }
  public void loginAsEmployee() {
    DataSourceContextHolder.setCurrentDb("db3");
  }
}
