package com.Ecommerce.project.Controller;

import com.Ecommerce.project.Utils.AuthUtils;
import com.Ecommerce.project.payload.OrderDTO;
import com.Ecommerce.project.payload.OrderRequestDTO;
import com.Ecommerce.project.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtils authUtils;

    @PostMapping("/order/users/payment/{paymentMethod}")
    public ResponseEntity<OrderDTO>orderProducts(@PathVariable String paymentMethod,
                                                 @RequestBody OrderRequestDTO orderRequestDTO){
        String emailId = authUtils.loggedInEmail();
        OrderDTO order = orderService.placeOrder(
                emailId,
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getAddressId(),
                orderRequestDTO.getPaymentMethod(),
                orderRequestDTO.getPgResponseMessage(),
                orderRequestDTO.getPgPaymentId()
        );

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
