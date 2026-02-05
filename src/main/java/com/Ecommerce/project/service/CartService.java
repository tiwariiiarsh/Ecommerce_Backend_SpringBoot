package com.Ecommerce.project.service;

import com.Ecommerce.project.model.CartItem;
import com.Ecommerce.project.payload.CartDTO;
import com.Ecommerce.project.payload.CartItemDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    public CartDTO addProductToCart(Long productId, Integer quantity) ;

    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId, Long cartId);

    @Transactional
    CartDTO updateProductQuantityInCarts(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);

    String createOrUpdateCartWithItems(List<CartItemDTO> cartItems);
}
