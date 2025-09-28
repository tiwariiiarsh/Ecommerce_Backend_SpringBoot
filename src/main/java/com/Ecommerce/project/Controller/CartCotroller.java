package com.Ecommerce.project.Controller;

import com.Ecommerce.project.Utils.AuthUtils;
import com.Ecommerce.project.model.Cart;
import com.Ecommerce.project.payload.CartDTO;
import com.Ecommerce.project.repositories.CartRepository;
import com.Ecommerce.project.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartCotroller {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtils authUtils;



    @Autowired
    private CartService cartService;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart( @PathVariable Long productId,
                                                   @PathVariable Integer quantity){
        CartDTO cartDTO = cartService.addProductToCart(productId,quantity);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getCarts(){
        List<CartDTO> cartDTOS = cartService.getAllCarts();
        return new ResponseEntity<List<CartDTO>>(cartDTOS,HttpStatus.FOUND);
    }
    @GetMapping("/carts/user/cart")
    public ResponseEntity<CartDTO>getCardById(){
        String emailId = authUtils.loggedInEmail();
        Cart cart =cartRepository.findCartByEmail(emailId);
        Long cartId = cart.getCartId();
        CartDTO cartDTO = cartService.getCart(emailId,cartId);//email aur cartId dono isiliye use kr rhe  jisse kl ko agr scalable bnana h user has two cart with same email
        return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK) ;
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId,
                                                     @PathVariable String operation){
        CartDTO cartDTO = cartService.updateProductQuantityInCarts(productId,
                operation.equalsIgnoreCase("delete")? -1:1);
        return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String>deleteProductFromCart(@PathVariable Long cartId,
                                                       @PathVariable Long productId){
        String status=cartService.deleteProductFromCart(cartId,productId);
        return new ResponseEntity<String>(status,HttpStatus.OK);

    }
}
