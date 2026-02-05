package com.Ecommerce.project.service;

import com.Ecommerce.project.payload.AnalyticsResponse;
import com.Ecommerce.project.repositories.OrderRepository;
import com.Ecommerce.project.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public AnalyticsResponse getAnalyticsData() {
        AnalyticsResponse response = new AnalyticsResponse();

        long productCount = productRepository.count();
        long totalOrders = orderRepository.count();
        Double totalRevenue = orderRepository.getTotalRevenue();

        response.setProductCount(String.valueOf(productCount));
        response.setTotalRevenue(String.valueOf(totalRevenue != null ? totalRevenue : 0));
        response.setTotalOrders(String.valueOf(totalOrders));

        return response;
    }
}
