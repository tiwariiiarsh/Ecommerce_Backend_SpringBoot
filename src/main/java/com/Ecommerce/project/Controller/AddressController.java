package com.Ecommerce.project.Controller;

import com.Ecommerce.project.Utils.AuthUtils;
import com.Ecommerce.project.model.User;
import com.Ecommerce.project.payload.AddressDTO;
import com.Ecommerce.project.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AddressController {

    @Autowired
    private  AddressService addressService;

    @Autowired
    AuthUtils authUtils;

    @PostMapping("/api/addresses")
    public ResponseEntity<AddressDTO>createAddress(
               @Valid @RequestBody AddressDTO addressDTO
    ){
        User user = authUtils.loggedInUser();
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO,user);
        return new ResponseEntity<>(addressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/api/addresses")
    public ResponseEntity<List<AddressDTO>>getAddresses(){
        List<AddressDTO> addressList = addressService.getAddresses();
        return new ResponseEntity<>(addressList,HttpStatus.OK);
    }

    @GetMapping("/api/addresses/{addressId}")
    public ResponseEntity<AddressDTO>getAddressesById(@PathVariable Long addressId){
        AddressDTO addressDTOS = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTOS,HttpStatus.OK);
    }

    @GetMapping("/api/user/addresses")
    public ResponseEntity<List<AddressDTO>>getUserAddresses(){
        User loginUser = authUtils.loggedInUser();
        List<AddressDTO> addressList = addressService.getUserAddresses(loginUser);
        return new ResponseEntity<>(addressList,HttpStatus.OK);
    }

    @PutMapping("/api/addresses/{addressId}")
    public ResponseEntity<AddressDTO>updateAddressById(@PathVariable Long addressId,@RequestBody AddressDTO addressDTO){
        AddressDTO addressDTOS = addressService.updateAddressById(addressId,addressDTO);
        return new ResponseEntity<>(addressDTOS,HttpStatus.OK);
    }
    @DeleteMapping("/api/addresses/{addressId}")
    public ResponseEntity<String>deleteAddress(@PathVariable Long addressId){
        String status = addressService.deleteAddressById(addressId);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }
}
