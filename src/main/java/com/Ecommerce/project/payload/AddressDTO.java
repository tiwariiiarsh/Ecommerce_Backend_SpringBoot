package com.Ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private Long addressId;
    public  String street;
    public String buildingName;
    public String city;
    public String state;
    public String country;
    public String pincode;

}
