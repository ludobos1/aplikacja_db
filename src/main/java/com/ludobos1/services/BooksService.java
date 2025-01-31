package com.ludobos1.services;

import com.ludobos1.encje.Book;
import com.ludobos1.encje.User;
import com.ludobos1.repositories.BooksRepository;
import com.ludobos1.repositories.CategoriesRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;

@Service
public class BooksService {
  @Autowired
  private BooksRepository booksRepository;
  @Autowired
  private CategoryService categoryService;
  private static Validator validator;
  static {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }
  public List<Book> getAllBooks() {
    return booksRepository.findAll();
  }
  public List<Book> getBooksByTitle(String title) {
    return booksRepository.findByTitle(title);
  }
  public List<Book> getBooksByAuthor(String author) {
    return booksRepository.findByAuthor(author);
  }
  public List<Book> getBooksByCategory(int categoryId) {
    return booksRepository.findByCategory_Id(categoryId);
  }
  public List<Book> getBooksByTitleOrAuthorOrCategory(String input) {
    List<Book> books = getBooksByTitle(input);
    books.addAll(getBooksByAuthor(input));
    if(categoryService.getCategoryByName(input) != null) {
      books.addAll(getBooksByCategory(categoryService.getCategoryByName(input).getId()));
    }
    return books;
  }
  public Book getBookById(int id) {
    return booksRepository.findById(id);
  }
  public Book saveBook(Book book) {
    return booksRepository.save(book);
  }
  @Transactional
  public String addBook(Book book) {
    try {
      validateBook(book);
    }catch (ConstraintViolationException e) {
      return "Error with data validation: " + e.getMessage();
    }
    booksRepository.save(book);
    return "Book updated";
  }
  public void validateBook(Book book) {
    Set<ConstraintViolation<Book>> violations = validator.validate(book);
    if (!violations.isEmpty()) {
      for (ConstraintViolation<Book> violation : violations) {
        System.out.println("Validation error: " + violation.getMessage());
      }
      throw new ConstraintViolationException(violations);
    }
  }
  public void deleteBook(Book book){
    booksRepository.delete(book);
  }
}
