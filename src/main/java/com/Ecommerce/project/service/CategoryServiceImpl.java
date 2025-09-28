package com.Ecommerce.project.service;

import com.Ecommerce.project.exceptions.ApiException;
import com.Ecommerce.project.exceptions.ResourceNotFoundException;
import com.Ecommerce.project.model.Category;
import com.Ecommerce.project.payload.CategoryDTO;
import com.Ecommerce.project.payload.CategoryResponse;
import com.Ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


//--------------<<<<<<<<<<<< @Service <<<<<<<<<<<<-------------------------
//@Service : ek specialized stereotype annotation hai
//Ye batata hai ki class ek business service layer ka part hai.
//Spring automatically is class ko Spring Bean bana kar ApplicationContext me register kar deta hai.
@Service
public class CategoryServiceImpl implements CategoryService {
    private Category category;

    @Autowired //   it will inject bean of one class in another class as dependency
    private CategoryRepository categoryRepository;
//    we will remove this categories list and use database and repository for CRUD Operation
//    private  List <Category> categories= new ArrayList<>();
//    giving it default id if user is not giving them id
//    private Long categoryId=1L;


    @Autowired
    private ModelMapper modelMapper;

//   ------------------  getAllCategories() --------------------------

//    jo code neeche diya hai wo ek Service layer ka method hai jo repository se data
//    la raha hai, DTO me convert kar raha hai aur response return kar raha hai.
@Override
//saari category DB se fetch krke CategroyResponse me store ho rha aur yhi se UI frontend me bhej denge
public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {
//    Sort.by(sortBy):- Ye ek Sort object banata hai jisme column ka naam diya hota hai (jaise "categoryName").
//    Uspar ascending/descending apply hota hai.

    Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")  //    Ignore case matlab ASC, asc, Asc sabko same treat karega.
            ?Sort.by(sortBy).ascending()
            :Sort.by(sortBy).descending();
// 1.Ye ek Pageable object banata hai jo Spring Data JPA ko batata hai ki kaunsa page aur kitne records chahiye.
    Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
//    Page<T> = Data + Metadata (pagination info)
    Page<Category>categoryPage = categoryRepository.findAll(pageDetails);
    List<Category> categories = categoryPage.getContent();

    // 1. Repository se saare categories fetch karo
//    List<Category> categories = categoryRepository.findAll();

    // 2. Agar database me ek bhi category nahi hai to custom exception throw karo
    if (categories.isEmpty()){
        throw new ApiException("No category created till now.");
    }

    // 3. ModelMapper ka use karke Category Entity ko CategoryDTO me convert karna
    List<CategoryDTO> categoryDTOS = categories.stream()
            .map(category -> modelMapper.map(category, CategoryDTO.class))
            .toList();

    // 4. CategoryResponse object banana aur usme DTO list set karna
    CategoryResponse categoryResponse = new CategoryResponse();
    categoryResponse.setContent(categoryDTOS);
    categoryResponse.setPageNumber(categoryPage.getNumber());
    categoryResponse.setPageSize(categoryPage.getSize());
    categoryResponse.setTotalElements(categoryPage.getTotalElements());
    categoryResponse.setTotalPages(categoryPage.getTotalPages());
    categoryResponse.setLastPage(categoryPage.isLast());
    // 5. Final response return kar dena
    return categoryResponse;
}



    //---------------------------createCategory(Category category)------------------------
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // Step 1: Check if category with same name already exists
        Category categoryFromDB = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if (categoryFromDB != null) {
            throw new ApiException("Category with the name " + categoryDTO.getCategoryName() + " already exists!");
        }

        // Step 2: DTO -> Entity
        Category category = modelMapper.map(categoryDTO, Category.class);

        // Step 3: Save to DB
        Category savedCategory = categoryRepository.save(category);

        // Step 4: Entity -> DTO (return response)
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId){

        Category deleteCategory= categoryRepository.findById(categoryId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"category is not found"));  ye bhi custom exception deta h
                .orElseThrow(() ->new ResourceNotFoundException("category","categoryId",categoryId)); // ye bhi custom exception dega through Global exception concept

          categoryRepository.delete(deleteCategory);
//        return "category with categoryID  "+categoryId+"   deleted Successfully";
        return  modelMapper.map(deleteCategory,CategoryDTO.class);
//        -------------------------OR------------------------------------
//        List<Category> categories = categoryRepository.findAll();
//        Category category = categories.stream()
//                .filter(c -> c.getCategoryId().equals(categoryId))
//                .findFirst()
////                .orElse(null);
//        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Resource are not found"));
//        categoryRepository.delete(category);
//// ---------->>>>>orElse(null) isliye use hua h jisse jb categories me category nhi  ho toh category  ko null show kre !!
////        it work as a check point for delete operation,stack underflow condition,like if no category is available--------<<<<<<<<<<<
//        if(category==null){
//            return "category not found";
//        }
//        return "category with categoryID  "+categoryId+"   deleted Successfully";
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Optional<Category>savedCategroyOptional = categoryRepository.findById(categoryId);
        Category savedCategory = savedCategroyOptional
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"category is not found"));
        .orElseThrow(() -> new ResourceNotFoundException("category","categoryId",categoryId));
        Category category=modelMapper.map(categoryDTO,Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
//  -----------------------------OR----------------------------------------
//        Optional use hota hai null safety ke liye.,,Agar tumne null check nahi kiya → NullPointerException aayega
//        is problem ko solve karne ke liye Optional<T> return karte hain.
//        List<Category> categories = categoryRepository.findAll();
//        Optional<Category> optionalCategory = categories.stream()
//                .filter(c -> c.getCategoryId().equals(categoryId))
//                .findFirst();
//        if (optionalCategory.isPresent()){
//            Category existingCategory = optionalCategory.get();
//            existingCategory.setCategoryName(category.getCategoryName());
//            Category savedCategory = categoryRepository.save(existingCategory);
//            return savedCategory ;
//        }else{
//            throw  new ResponseStatusException(HttpStatus.NOT_FOUND,"Category are not found");
//        }
////


    }
}
