package com.Ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long ProductId;
    private String ProductName;
    private  String description;
    private String image;
    private Integer quantity;
    private Double discount;
    private Double price;
    private  Double specialPrice;

}
