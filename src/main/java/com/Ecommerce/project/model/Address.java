package com.Ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5,message = "street name must be atleast 5 characters")
    public  String street;

    @NotBlank
    @Size(min = 5,message = "building name must be atleast 5 characters")
    public String buildingName;

    @NotBlank
    @Size(min = 4,message = "city name must be atleast 4 characters")
    public String city;

    @NotBlank
    @Size(min = 2,message = "state name must be atleast 2 characters")
    public String state;

    @NotBlank
    @Size(min = 2,message = "country name must be atleast 2 characters")
    public String country;

    @NotBlank
    @Size(min = 5,message = "Pincode  must be atleast 5 characters")
    public String pincode;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
//    Yeh specify karta hai ki ek Address object ke saath kin kin Users ne isse use kiya hai uski list yaha milegi

    public Address(String street,String buildingName,String city,String state,String country,String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country =country;
        this.pincode = pincode;

    }
}
