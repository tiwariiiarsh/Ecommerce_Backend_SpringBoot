package com.Ecommerce.project.service;
import com.Ecommerce.project.exceptions.ApiException;
import com.Ecommerce.project.exceptions.ResourceNotFoundException;
import com.Ecommerce.project.model.*;
import com.Ecommerce.project.payload.OrderDTO;
import com.Ecommerce.project.payload.OrderItemsDTO;
import com.Ecommerce.project.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    CartRepository  cartRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartService cartService;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    @Override
    public OrderDTO placeOrder(String emailId,String pgStatus, String paymentMethod, Long addressId, String pgName, String pgResponseMessage, String pgPaymentId) {
//        Getting the user cart
        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart==null){
            throw new ResourceNotFoundException("Cart","email",emailId);
        }
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("address","addressId",addressId));
//        create a new order with payment info
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setEmail(emailId);
        order.setTotalAmount(cart.getTotalPrice());
        order.setAddress(address);
        order.setOrderStatus("Order Accepted!!");

        Payment payment=new Payment(paymentMethod,pgPaymentId,pgName,pgResponseMessage,pgStatus);
//        Bi-directional relationship (Order ↔ Payment) maintain ho jata hai.
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);
        Order savedOrder = orderRepository.save(order);
//        Get items from cart into the order item
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()){
            throw new ApiException("Cart is empty!!");
        }
        List<OrderItems>orderItem = new ArrayList<>();
        for (CartItem cartItem:cartItems){
          OrderItems orderItems = new OrderItems();
          orderItems.setProduct(cartItem.getProduct());
          orderItems.setQuantity(cartItem.getQuantity());
          orderItems.setDiscount(cartItem.getDiscount());
          orderItems.setOrderedProductPrice(cartItem.getProductPrice());
          orderItems.setOrder(savedOrder);
          orderItem.add(orderItems);

        }
        orderItem = orderItemRepository.saveAll(orderItem);
//        Update product stock-->total product quantity me se cart item ke andr jitne product quantity thi utni quantity kam ho jaegi product Quantity me se
        cart.getCartItems().forEach(item ->{
            int quantity = item.getQuantity();
            Product product = item.getProduct();
//            product.setQuantity(item.getQuantity() - quantity);
            product.setQuantity(product.getQuantity() - quantity);

            productRepository.save(product);

//            clear cart
            cartService.deleteProductFromCart(cart.getCartId(),item.getProduct().getProductId());
        });

//        send  back the summary
        OrderDTO orderDTO = modelMapper.map(savedOrder,OrderDTO.class);
//orderDTO ke andr OrderItemsDTO add kr rhe h
        orderItem.forEach(item ->
                orderDTO.getOrderItems().add(
                        modelMapper.map(item, OrderItemsDTO.class)
                ));
        orderDTO.setAddressId(addressId);
        return orderDTO;
    }

}
