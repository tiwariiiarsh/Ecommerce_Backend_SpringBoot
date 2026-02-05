package com.Ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //incase of IDENTITY-->> database dependent(used in Mysql) --> auto increement,, AUTO--> Hibernate dependent ->use in anyother multiple db
    private Long productId;

    @NotBlank
    @Size(min = 3,message = "Product name must contain atleast 3 characters")
    private String productName;

    @NotBlank
    @Size(min = 6,message = "Product description must contain atleast 6 characters")
    private String description;

    private Integer quantity;
    private Double price; //100
    private Double specialPrice;  //75
    private Double discount; //25
    private String image;
//    specialPrice = price-discount
//    100-(25/100)*100

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; //one category has multiple product

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;

    @OneToMany(mappedBy = "product",cascade = {CascadeType.PERSIST,CascadeType.MERGE},fetch = FetchType.EAGER)
    private List<CartItem> cartItem = new ArrayList<>();
}
