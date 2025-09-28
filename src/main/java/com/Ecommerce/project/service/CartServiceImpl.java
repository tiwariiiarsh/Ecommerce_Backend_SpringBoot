package com.Ecommerce.project.service;

import com.Ecommerce.project.Utils.AuthUtils;
import com.Ecommerce.project.exceptions.ApiException;
import com.Ecommerce.project.exceptions.ResourceNotFoundException;
import com.Ecommerce.project.model.Cart;
import com.Ecommerce.project.model.CartItem;
import com.Ecommerce.project.model.Product;
import com.Ecommerce.project.payload.CartDTO;
import com.Ecommerce.project.payload.ProductDTO;
import com.Ecommerce.project.repositories.CartItemRepository;
import com.Ecommerce.project.repositories.CartRepository;
import com.Ecommerce.project.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {
//    @Autowired
//    CartItem cartItem;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

//    @Autowired
//    ProductDTO productDTO;

    @Autowired
    AuthUtils authUtils;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
//        find existing cart or create one
        Cart  cart = createCart();
//           retreive product Detils
        Product  product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("product","productId",productId));

//       perform validation
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(),
                productId
        );

        if (cartItem!=null){
            throw new  ApiException("product"+product.getProductName()+"already exists!");
        }
        if (product.getQuantity()==0){
            throw new ApiException(product.getProductName()+"is not available");
        }
        if (product.getQuantity()<quantity){
            throw  new ApiException("Please, make an order of the"+product.getProductName()+"less than or equal to quantity"+product.getQuantity()+".");
        }

//        create cart item
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

//        save cartItem
        cartItemRepository.save(newCartItem);

//      product is added to cartItem then productQuantity is same we will reduce it if payment is done
        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice()*quantity));
        cartRepository.save(cart);
//        return the updated cart
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        List<CartItem> cartItems= cart.getCartItems();
        Stream<ProductDTO>productStream = cartItems.stream().map(item -> {
                    ProductDTO map = modelMapper.map(item.getProduct(),ProductDTO.class);
                    map.setQuantity(item.getQuantity());
                    return map;
                }
        );
        cartDTO.setProducts(productStream.toList());
        return cartDTO;


    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.size() == 0) {
            throw new ApiException("No cart exists");
        }

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                productDTO.setQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
                return productDTO;
            }).collect(Collectors.toList());


            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(Collectors.toList());

        return cartDTOs;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId,cartId);
        if (cart==null){
            throw new ResourceNotFoundException("Cart","CartId",cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(),ProductDTO.class))
                .collect(Collectors.toList());
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCarts(Long productId, Integer quantity) {
        String emailId = authUtils.loggedInEmail();
        Cart cartCart=cartRepository.findCartByEmail(emailId);
        Long cartId = cartCart.getCartId();
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart","cartId",cartId));

        Product  product = productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("product","productId",productId));

//       perform validation
        if (product.getQuantity()==0){
            throw new ApiException(product.getProductName()+"is not available");
        }
        if (product.getQuantity()<quantity){
            throw  new ApiException("Please, make an order of the"+product.getProductName()+"less than or equal to quantity"+product.getQuantity()+".");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if (cartItem==null){
            throw  new ApiException("Product"+product.getProductName()+"not available in cart");
        }
        int newQuantity = cartItem.getQuantity()+quantity;
        if (newQuantity<0){
            throw new ApiException("The resulting Quantity cannot be negative");
        }
        if (newQuantity==0){
            deleteProductFromCart(cartId,productId);
        }else {
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cartItem.setProductPrice(product.getSpecialPrice());
            cart.setTotalPrice((cart.getTotalPrice()) + (cartItem.getProductPrice()) * quantity);
            cartRepository.save(cart);
        }
        CartItem updatedItem = cartItemRepository.save(cartItem);
        if (updatedItem.getQuantity()==0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem>cartItems = cart.getCartItems();
        Stream<ProductDTO>productStream = cartItems.stream().map(item -> {
                    ProductDTO map = modelMapper.map(item.getProduct(),ProductDTO.class);
                    map.setQuantity(item.getQuantity());
                    return map;
                }
        );
        cartDTO.setProducts(productStream.toList());
        return cartDTO;

    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new ApiException("Product " + product.getProductName() + " not available in the cart!!!");
        }
//1000-(100*2) total me se old productprice reduce
        double cartPrice = cart.getTotalPrice()
                - (cartItem.getProductPrice() * cartItem.getQuantity());
//200  set current new product price
        cartItem.setProductPrice(product.getSpecialPrice());

//        800+(200*2)=1200     add new product price in cart total price
        cart.setTotalPrice(cartPrice
                + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);
    }

    private Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtils.loggedInEmail());
        if(userCart!=null){
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtils.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        return newCart;
    }
}
