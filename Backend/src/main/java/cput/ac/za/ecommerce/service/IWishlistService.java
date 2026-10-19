package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.response.WishlistItemResponse;

import java.util.List;

public interface IWishlistService {
    List<WishlistItemResponse> list(String customerEmail);
    WishlistItemResponse add(String customerEmail, String productId);
    void remove(String customerEmail, String productId);
}
