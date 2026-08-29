package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.domain.ProductCatalog;
import cput.ac.za.ecommerce.request.ProductCatalogRequest;

import java.util.List;

public interface IProductCatalogService {

    ProductCatalog createProduct(ProductCatalogRequest request);

    ProductCatalog getProductCatalogById(String productId);

    List<ProductCatalog> getAllProductCatalogs();

    ProductCatalog updateProduct(
            String productId,
            ProductCatalogRequest request
    );

    void deleteProductCatalog(String productId);
}