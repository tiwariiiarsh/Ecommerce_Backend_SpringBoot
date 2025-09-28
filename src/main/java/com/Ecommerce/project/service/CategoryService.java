package com.Ecommerce.project.service;

import com.Ecommerce.project.payload.CategoryDTO;
import com.Ecommerce.project.payload.CategoryResponse;

//interface is used instead of class bcz it will use and promote modularity and loose coupling concept
public interface CategoryService {
//    List<Category> getAllCategories();   this is used when no DTO concept,now we have this list of category in
//    CategoryResponse and we access all response for user from DB from this CategoryResponse class
    CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder);
//    void createCategory(Category category);
    CategoryDTO  createCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO categoryDTO,Long categoryId);
}
