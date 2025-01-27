package com.ludobos1.encje;

import com.ludobos1.encje.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "users")
public class User {
  @NotBlank(message = "Nazwa użytkownika nie może być pusta")
  @Size(min = 3, max = 20, message = "Nazwa użytkownika musi mieć od 3 do 20 znaków")
  @Pattern(regexp = "^[a-zA-Z0-9_.!]*$", message = "Dozwolone znaki: litery, cyfry, '_', '.', '!'")
  private String username;
  @NotBlank(message = "Hasło nie może być puste")
  //@Size(min = 3, max = 20, message = "Hasło musi mieć od 3 do 20 znaków")
  //@Pattern(regexp = "^[a-zA-Z0-9_.!]*$", message = "Dozwolone znaki: litery, cyfry, '_', '.', '!'")
  private String password;
  @NotBlank(message = "E-mail nie może być pusty")
  @Email(message = "Nieprawidłowy format e-maila")
  private String email;
  @Enumerated(EnumType.STRING)
  private Role role;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Transient
  public final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }
  @PrePersist
  public void hashPassword(){
    this.password = passwordEncoder.encode(this.password);
  }
}

