package com.ludobos1.services;

import com.ludobos1.encje.Category;
import com.ludobos1.repositories.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
  @Autowired
  private CategoriesRepository CategoriesRepository;
  public List<Category> getAllCategories() {
    return CategoriesRepository.findAll();
  }
}
