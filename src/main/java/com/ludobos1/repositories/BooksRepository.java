package com.ludobos1.repositories;

import com.ludobos1.encje.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {
  List<Book> findByAuthor(String author);
  List<Book> findByTitle(String title);
  Book findById(int id);
  List<Book> findByCategory_Id(int categoryId);
}
