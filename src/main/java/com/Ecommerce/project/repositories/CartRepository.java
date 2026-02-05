package com.Ecommerce.project.repositories;

import com.Ecommerce.project.model.Cart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    @Query("SELECT c FROM Cart c WHERE  c.user.email = ?1") //?1 means 1st parameter in below method
    Cart findCartByEmail(String Email);

    @Query("SELECT c FROM Cart c WHERE  c.user.email = ?1 AND c.id = ?2")
    Cart findCartByEmailAndCartId(String emailId, Long cartId);

    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product  p WHERE p.id=?1")
    List<Cart> findCartsByProductId(Long productId);



}
