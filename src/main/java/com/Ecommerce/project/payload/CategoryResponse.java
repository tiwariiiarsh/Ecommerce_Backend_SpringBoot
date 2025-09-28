package com.Ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
//-------------------NOTES----------------------------------
//Response Wrapping
//Saari CategoryDTO objects ko ek wrapper CategoryResponse me store kiya jaata hai.
//Isme future me aur bhi info add kar sakte ho jaise pageNumber, totalPages, totalElements, etc.
//Ye CategoryResponse return hota hai Controller ko → Controller usko API response bana ke frontend ko bhej deta hai.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
//    CategoryDTO send the data from client to server through categoryDTO class
    private List<CategoryDTO> content;
    private Integer pageNumber;
    private  Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
