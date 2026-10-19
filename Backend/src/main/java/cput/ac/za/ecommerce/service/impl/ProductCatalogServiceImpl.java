package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.ProductCatalog;
import cput.ac.za.ecommerce.factory.ProductCatalogFactory;
import cput.ac.za.ecommerce.repository.ProductCatalogRepository;
import cput.ac.za.ecommerce.request.ProductCatalogRequest;
import cput.ac.za.ecommerce.service.IProductCatalogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCatalogServiceImpl
        implements IProductCatalogService {

    private final ProductCatalogRepository repository;

    public ProductCatalogServiceImpl(
            ProductCatalogRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public ProductCatalog createProduct(
            ProductCatalogRequest request
    ) {

        if (repository.existsBySkuIgnoreCase(request.getSku())) {
            throw new IllegalArgumentException(
                    "A product with SKU '" +
                            request.getSku() +
                            "' already exists."
            );
        }

        ProductCatalog product =
                ProductCatalogFactory.createProduct(request);

        return repository.save(product);
    }

    @Override
    public ProductCatalog getProductCatalogById(
            String productId
    ) {

        return repository.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found."
                        ));
    }

    @Override
    public List<ProductCatalog> getAllProductCatalogs() {

        return repository.findAllByActiveTrueOrderByProductNameAsc();
    }

    @Override
    public ProductCatalog updateProduct(
            String productId,
            ProductCatalogRequest request
    ) {

        ProductCatalog existing =
                repository.findById(productId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Product not found."
                                ));

        repository.findBySkuIgnoreCase(request.getSku())
                .ifPresent(product -> {
                    if (!product.getProductId()
                            .equals(existing.getProductId())) {
                        throw new IllegalArgumentException(
                                "SKU already exists."
                        );
                    }
                });

        ProductCatalog updated =
                ProductCatalogFactory.createProduct(
                        existing.getProductId(),
                        request
                );

        return repository.save(updated);
    }

    @Override
    public void deleteProductCatalog(
            String productId
    ) {

        ProductCatalog existing =
                repository.findById(productId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Product not found."
                                ));

        existing.deactivate();

        repository.save(existing);
    }
}