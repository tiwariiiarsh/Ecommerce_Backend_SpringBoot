package com.Ecommerce.project.payload;

import com.Ecommerce.project.model.Address;
import lombok.Data;

import java.util.Map;

@Data
public class StripePaymentDTO {
    private Long amount;
    private String  currency;
    private String email;
    private String name;
    private Address address;
    private String description;
    private Map<String,String>metaData;

}
