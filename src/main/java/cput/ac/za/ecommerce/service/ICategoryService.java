package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.domain.ProductCategory;

import java.util.List;

public interface ICategoryService {
    List<ProductCategory> getAllCategories();
}
