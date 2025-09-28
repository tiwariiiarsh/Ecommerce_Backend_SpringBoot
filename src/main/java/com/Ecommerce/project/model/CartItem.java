package com.Ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "cart_Items")
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

//    Example:
//            👤 User: Arsh
//🛒 Cart: Arsh ka Cart
//    CartItem #1 → Product = iPhone 15, Quantity = 1
//    CartItem #2 → Product = Shoes, Quantity = 3
//    CartItem #3 → Product = Laptop, Quantity = 2
//            👉 Abhi Arsh ke cart me 3 CartItems hain.
//    Lekin agar Arsh aur products add karta hai to naye CartItems banenge.
//
//    Relation summary:
//    User (1) → Cart (1)
//    Cart (1) → CartItems (Many)
//    CartItem (Many) → Product (1)
//

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long cartItemId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private Double discount;
    private Double productPrice;


}
