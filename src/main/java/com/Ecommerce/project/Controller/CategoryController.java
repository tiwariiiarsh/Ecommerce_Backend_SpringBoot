package com.Ecommerce.project.Controller;

import com.Ecommerce.project.config.AppConstants;
import com.Ecommerce.project.payload.CategoryDTO;
import com.Ecommerce.project.payload.CategoryResponse;
import com.Ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//----------<<<<<<<< NOTE <<<<<<<<<<<---------------------
//Initially i added some try catch and also some other code bcz its in learning process
//but now have MyGlobalExceptionHandler class which handle all exception, now i was commented out all unusable code


//   ------>>>----Use of  @RequestMapping at Method level----->>>>>>-----------
//it will remove "/api" from pathvariable in Mapping Annotation used in below methods and make public for all of them
//@RequestMapping("/api")

@RestController
public class CategoryController {
//    Field injection
    @Autowired
  private CategoryService categoryService ;
//    USE field injection or constructor injection
//  constructor injection
//    public CategoryController(CategoryService categoryService) {
//        this.categoryService = categoryService;
//    }



//    ------>>>----Use of  @RequestMapping at Method level----->>>>>>-----------
//    @RequestMapping(value = "/api/public/categories",method = RequestMethod.GET)
//    OR we can use @GetMapping("/api/public/categories")  both will do same work

//    -------------------->>>>>>>>>  @GetMapping >>>>>>>>>>>>-------------------------
//    pagination is done
    @GetMapping("/api/public/categories")
    public ResponseEntity<CategoryResponse>getAllCategories(
            @RequestParam(name="pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_CATEGORY_BY,required = false) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder
    ){
       CategoryResponse categoryResponse =categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(categoryResponse,HttpStatus.OK);
    }


//-------------------->>>>>>>>>  @PostMapping >>>>>>>>>>>>-------------------------
    @PostMapping("/api/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }
//agr msg bhejna h dto nhi return krna toh
//        return new ResponseEntity<>("category added succesfully!!",HttpStatus.CREATED);

//    ------------------>>>@Valid>>>>------------------------
//    @Valid → Ensures that object follows validation rules (e.g., not null, email format, min age).
//    If any field does not follow validation, Spring will throw:
//            👉 MethodArgumentNotValidException
//    and return a 400 Bad Request response with validation error details.

//    ----------->>>>>>>>>>>@RequestBody>>>>>>>>>---------------
//    MOSTLY use in post and put
//        Spring Boot me jab tum JSON data bhejte ho POST request ke body me,
//        aur method parameter pe @RequestBody lagate ho, toh Spring ka Jackson library
//        automatically us JSON ko tumhari Category class ke object me convert kar deta hai.


//----------------->>>>>>>>>>>>@PathVariable>>>>>>>----------------
//    Ye URL ke andar se value extract karta hai.
//    Mostly GET, DELETE, PUT me use hota hai.
//    Example: /categories/101 → yaha 101 path se nikalega aur tumhare method parameter me set karega.

//-------------------->>>>>>>>>  @DeleteMapping >>>>>>>>>>>>-------------------------
    @DeleteMapping("/api/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
    //ResponseEntity<T> ek HTTP response wrapper hai jo tumhe status code, headers aur body tino customize karne deta hai.
    //Normally agar tum controller se Object return karte ho toh Spring default 200 OK bhej deta hai. Lekin ResponseEntity use karke tum exact decide kar sakte ho ki:
    //Body kya bhejni hai
    //HTTP status code kya bhejna hai (200, 201, 404, 500 etc.)
    //Extra headers bhejne hain ya nahi

//  ResponseStatusException:Ye ek exception class hai jo directly Spring ke error response me
//  HTTP status code + message bhej deti hai.
//        try{
            CategoryDTO status = categoryService.deleteCategory(categoryId);
//            these 3 method given below give same result------->>>>>>>>>>
            return new ResponseEntity<>(status, HttpStatus.OK);   //this one is commonly used
//            return ResponseEntity.ok(status);
//            return ResponseEntity.status(HttpStatus.OK).body(status);
//        }catch (ResponseStatusException e){
//            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
//        }

    }

// -------------------->>>>>>>>>  @PutMapping >>>>>>>>>>>>-------------------------
    @PutMapping("api/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                 @PathVariable Long categoryId) {
//        try {
            CategoryDTO savedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
            return new ResponseEntity<>(savedCategoryDTO,HttpStatus.OK);
//            return new ResponseEntity<>("category with categoryId: " + categoryDTO.getCategoryId() +
//                    " & have categoryName: " + category.getCategoryName(), HttpStatus.OK);   //this one is commonly used and use when you need a string return type not DTO return type
////
//        } catch (ResponseStatusException e) {
//            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
//        }
    }
}
