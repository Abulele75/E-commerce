package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.domain.ProductCatalog;
import cput.ac.za.ecommerce.request.ProductCatalogRequest;
import cput.ac.za.ecommerce.service.IProductCatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productcatalog")
public class ProductCatalogController {

    private final IProductCatalogService service;

    public ProductCatalogController(
            IProductCatalogService service
    ) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductCatalog> createProduct(
            @Valid
            @RequestBody
            ProductCatalogRequest request
    ) {

        ProductCatalog product =
                service.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(product);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductCatalog> getProductById(
            @PathVariable String productId
    ) {

        return ResponseEntity.ok(
                service.getProductCatalogById(productId)
        );
    }

    @GetMapping
    public ResponseEntity<List<ProductCatalog>> getAllProducts() {

        return ResponseEntity.ok(
                service.getAllProductCatalogs()
        );
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductCatalog> updateProduct(
            @PathVariable String productId,
            @Valid
            @RequestBody
            ProductCatalogRequest request
    ) {

        ProductCatalog updated =
                service.updateProduct(
                        productId,
                        request
                );

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String productId
    ) {

        service.deleteProductCatalog(productId);

        return ResponseEntity.noContent().build();
    }
}