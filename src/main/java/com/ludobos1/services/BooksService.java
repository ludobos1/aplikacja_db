package com.ludobos1.services;

import com.ludobos1.encje.Book;
import com.ludobos1.repositories.BooksRepository;
import com.ludobos1.repositories.CategoriesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BooksService {
  @Autowired
  private BooksRepository booksRepository;
  @Autowired
  private CategoriesRepository categoriesRepository;
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
    if(categoriesRepository.findByName(input) != null) {
      books.addAll(getBooksByCategory(categoriesRepository.findByName(input).getId()));
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
  public Book updateBook(Book book) {
    return booksRepository.save(book);
  }
}
