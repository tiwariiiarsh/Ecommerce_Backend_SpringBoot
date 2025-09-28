package com.Ecommerce.project.repositories;


import com.Ecommerce.project.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


//we can also extends  to CrudRepository but when we are using Jpa then  use JpaRepository as we done
//and they both provides us some inbuilt method for crud operation which help us
//JpaRepository<Entity Type,primary key Type>
//JPA will give some inbuilt query creater like...save,saveAll,saveById.....etc
@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
//------------>>>>>>>Custom Query CREATER<<<<<<<<<<<<<<-----------
//    Tumne khud SQL query nahi likhni.
//    Spring Data JPA apne method name convention ko padhta hai:
//    findBy → iska matlab hai "database me query karke do"
//    CategoryName → entity me ek field hai categoryName
//    JPA automatically isko translate karta hai into query:
    Category findByCategoryName(String categoryName);
}
