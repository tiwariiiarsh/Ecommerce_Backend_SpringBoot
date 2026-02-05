package com.Ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

//------------<<<<<<<<<<<  @Entity  <<<<<<<<-------------------------------
// it will combine all @ annotations
//Jab aap Spring Boot + JPA use karte ho aur kisi class pe @Entity lagate ho,
// to Hibernate us class ke liye ek table bana deta hai (agar already nahi hai).
@Entity(name="categories")  //name is table name
@Data  // it will combine all @Getter @Setter @AllArgsConstructor @NoArgsConstructor annotations
@Getter // it will make Getter automatically
@Setter   // it will make Setter automatically
@AllArgsConstructor  // it will make Constructor with argument automatically
@NoArgsConstructor   // it will make Constructor with no argument automatically
public class Category {
//    @Id : makes it primary key in database
//    @GeneratedValue bolta hai → “Id khud se generate karna, developer ko manually value nahi deni.”
//    4 different cases in it :IDENTITY,AUTO,SEQUENCE,TABLE
//    IN CASE OF IDENTITY:it will automatically increment the id value
//   IN CASE OF AUTO:  Hibernate chooses the strategy automatically
//    
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

//    cannot leave blank from now ,it is from jpa,hibernate validation
    @NotBlank(message = "CategoryName should be present,Please Enter Category Name")
    @Size(min=5,message = "Category name must contains atleast 5 characters")
    private  String categoryName;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL)
    private List<Product> products;
}
