package com.Ecommerce.project.service;

import com.Ecommerce.project.exceptions.ApiException;
import com.Ecommerce.project.exceptions.ResourceNotFoundException;
import com.Ecommerce.project.model.Cart;
import com.Ecommerce.project.model.Category;
import com.Ecommerce.project.model.Product;

import com.Ecommerce.project.payload.CartDTO;
import com.Ecommerce.project.payload.ProductDTO;
import com.Ecommerce.project.payload.ProductResponse;
import com.Ecommerce.project.repositories.CartRepository;
import com.Ecommerce.project.repositories.CategoryRepository;
import com.Ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    CartService cartService;

    @Autowired
    CartRepository  cartRepository;

//    ye object create hua h
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${product.image}")
    private String path;

    @Value("${image.base.url}")
    private String imageBaseUrl;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
//        check if the product is already present or not!!
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categorey","categroyId",categoryId));

        boolean isProductNotPresent = true;
        List<Product>products = category.getProducts();
        for(Product value:products){
            if(value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
//yha pr sirf whi set hota h jo postman ya client ke through nhi diya jata
//        baaki jaise productName,description,quantity,price,discount h ye sbb already DTO me save ho chuka h
        if(isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
//            ye sare data ko DTO se entity me save kr rhi h
            return modelMapper.map(savedProduct, ProductDTO.class);
        }else{
            throw new ApiException("Product Already exist!!");
        }
    }

    private String constructImageUrl(String imageName) {
        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName : imageBaseUrl + "/" + imageName;
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String category) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")  //    Ignore case matlab ASC, asc, Asc sabko same treat karega.
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
// 1.Ye ek Pageable object banata hai jo Spring Data JPA ko batata hai ki kaunsa page aur kitne records chahiye.
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
//    Page<T> = Data + Metadata (pagination info)

//       Dynamic filtering using Specification
        Specification<Product> spec = Specification.where(null);
        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%"));
        }

        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("category").get("categoryName"), category));
        }

        Page<Product> pageProducts = productRepository.findAll(spec, pageDetails);
        List<Product> products =pageProducts.getContent();

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> {
                    ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                    productDTO.setImage(constructImageUrl(product.getImage()));
                    return productDTO;
                })
                .toList();


//        check is product is zero 0
//        if(products.isEmpty()){
//            throw new ApiException("product is not present!!");
//        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productResponse.getPageNumber());
        productResponse.setLastPage(productResponse.isLastPage());
        productResponse.setTotalPages(productResponse.getTotalPages());
        productResponse.setTotalElements(productResponse.getTotalElements());
        productResponse.setPageSize(productResponse.getPageSize());
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categorey","categroyId",categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")  //    Ignore case matlab ASC, asc, Asc sabko same treat karega.
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
// 1.Ye ek Pageable object banata hai jo Spring Data JPA ko batata hai ki kaunsa page aur kitne records chahiye.
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
//    Page<T> = Data + Metadata (pagination info)
        Page<Product> PageProducts = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);
        List<Product> products =PageProducts.getContent();

        if(products.isEmpty()){
            throw new ApiException(category.getCategoryName()+" category does not have any products");
        }
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productResponse.getPageNumber());
        productResponse.setLastPage(productResponse.isLastPage());
        productResponse.setTotalPages(productResponse.getTotalPages());
        productResponse.setTotalElements(productResponse.getTotalElements());
        productResponse.setPageSize(productResponse.getPageSize());
        return productResponse;
    }

//    SQL LIKE pattern ka funda
//Agar tu LIKE 'Lap%' use karega → "Laptop", "LapDesk" milenge.
//Agar tu LIKE '%Lap%' use karega → "Laptop", "MyLaptop", "BigLapDesk" sab milenge.



    @Override
    public ProductResponse searchProductByKeyword(String keyword,Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")  //    Ignore case matlab ASC, asc, Asc sabko same treat karega.
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
// 1.Ye ek Pageable object banata hai jo Spring Data JPA ko batata hai ki kaunsa page aur kitne records chahiye.
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
//    Page<T> = Data + Metadata (pagination info)
        Page<Product> PageProducts = (Page<Product>) productRepository.findByProductNameLikeIgnoreCase(""+ keyword +"%",pageDetails);
        List<Product>products = PageProducts.getContent();
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        if(products.isEmpty()){
            throw new ApiException("product not found with keyword: "+keyword);
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productResponse.getPageNumber());
        productResponse.setLastPage(productResponse.isLastPage());
        productResponse.setTotalPages(productResponse.getTotalPages());
        productResponse.setTotalElements(productResponse.getTotalElements());
        productResponse.setPageSize(productResponse.getPageSize());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product productFromDB =productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("category","categoryId",productId));

        Product product = modelMapper.map(productDTO,Product.class);
        productFromDB.setProductName(product.getProductName());
        productFromDB.setDescription(product.getDescription());
        productFromDB.setPrice(product.getPrice());
        productFromDB.setDiscount(product.getDiscount());
        productFromDB.setQuantity(product.getQuantity());
         productFromDB.setSpecialPrice(product.getSpecialPrice());

        Product savedProduct = productRepository.save(productFromDB);
        List<Cart>carts = cartRepository.findCartsByProductId(productId);
        List<CartDTO> cartDTOs = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
                    List<ProductDTO>products = cart.getCartItems().stream()
                            .map(p -> modelMapper.map(p.getProduct(),ProductDTO.class))
                            .collect(Collectors.toList());
                    cartDTO.setProducts(products);
                    return cartDTO;
                }).collect(Collectors.toList());
        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product deleteProduct =productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("category","categoryId",productId));
//        DELETE
        List<Cart>carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(),productId));
        productRepository.delete(deleteProduct);
        return modelMapper.map(deleteProduct,ProductDTO.class);
    }



    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
//        Get product from DB'
        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product","productId",productId));
//        Upload image to server
//        Get file name of uploaded image
        String path = "images/";
        String fileName =fileService.uploadImage(path,image);


//        updating the new file name to the product
        productFromDB.setImage(fileName);
//        save the updated product
        Product updatedProduct = productRepository.save(productFromDB);
//        return DTO after mapping product to DTO
        return  modelMapper.map(updatedProduct,ProductDTO.class);
    }
}
