package com.Ecommerce.project.payload;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private  Long categoryId;
    private  String categoryName;
}
