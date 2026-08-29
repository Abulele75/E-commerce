package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.ProductCategory;
import cput.ac.za.ecommerce.service.ICategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Override
    public List<ProductCategory> getAllCategories() {
        return List.of(ProductCategory.values());
    }
}
