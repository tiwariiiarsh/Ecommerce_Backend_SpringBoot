package com.Ecommerce.project.service;

import com.Ecommerce.project.payload.OrderDTO;
import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderDTO placeOrder(String emailId,String pgStatus, String paymentMethod, Long addressId, String pgName, String pgResponseMessage, String pgPaymentId);
}
