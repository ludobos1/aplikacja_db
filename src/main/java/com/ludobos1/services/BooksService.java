package com.ludobos1.services;

import com.ludobos1.encje.Book;
import com.ludobos1.repositories.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BooksService {
  @Autowired
  private BooksRepository booksRepository;
  public List<Book> getAllBooks() {
    return booksRepository.findAll();
  }
  public List<Book> getBooksByTitle(String title) {
    return booksRepository.findByTitle(title);
  }
  public List<Book> getBooksByAuthor(String author) {
    return booksRepository.findByAuthor(author);
  }
  public List<Book> getBooksByTitleOrAuthor(String input) {
    List<Book> books = getBooksByTitle(input);
    books.addAll(getBooksByAuthor(input));
    return books;
  }
  public Book getBookById(int id) {
    return booksRepository.findById(id);
  }
}
