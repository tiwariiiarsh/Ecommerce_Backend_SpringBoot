package com.Ecommerce.project.service;

import com.Ecommerce.project.exceptions.ResourceNotFoundException;
import com.Ecommerce.project.model.Address;
import com.Ecommerce.project.model.User;
import com.Ecommerce.project.payload.AddressDTO;
import com.Ecommerce.project.repositories.AddressRepository;
import com.Ecommerce.project.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private AddressRepository addressRepository;


    @Autowired
    UserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO,Address.class);
//        User and Address are many to one relationship h
//        1.user me address list dal rhe h
        List<Address>addressList = user.getAddresses();//address list of user address
        addressList.add(address); //nya address address list me add krega
        user.setAddresses(addressList); //user me updated address list set kr rha nya
//        2. address me user  dal  rhe h
        address.setUser(user); //address me user bhi put krega
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address>addressList = addressRepository.findAll();
        List<AddressDTO>addressDTOS =    addressList.stream().map(address ->
                modelMapper.map(address,AddressDTO.class))
                .collect(Collectors.toList());
        return addressDTOS;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "AddressId", addressId));
        AddressDTO addressDTO =modelMapper.map(address,AddressDTO.class);
        return addressDTO;
    }

    @Override
    public List<AddressDTO> getUserAddresses(User loginUser) {
        List<Address>addressList = loginUser.getAddresses();
        List<AddressDTO>addressDTOS =    addressList.stream().map(address ->
                        modelMapper.map(address,AddressDTO.class))
                .collect(Collectors.toList());
        return addressDTOS;

    }

    @Override
    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {
//        update address of addressId
        Address addressFromDB = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "AddressId", addressId));

        addressFromDB.setCity(addressDTO.getCity());
        addressFromDB.setState(addressDTO.getState());
        addressFromDB.setPincode(addressDTO.getPincode());
        addressFromDB.setCountry(addressDTO.getCountry());
        addressFromDB.setBuildingName(addressDTO.getBuildingName());
        addressFromDB.setStreet(addressDTO.getStreet());
        Address updatedAddress = addressRepository.save(addressFromDB);
//        now add the updated address in user
        User user = addressFromDB.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);
        return modelMapper.map(updatedAddress,AddressDTO.class);
    }

    @Override
    public String deleteAddressById(Long addressId) {
        Address addressFromDB =addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("address","addressId",addressId));
//        DELETE
        User user = addressFromDB.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);
//        addressRepository.deleteById(addressId); this is also true
        addressRepository.delete(addressFromDB);
        return  "Address with addressId "+addressId+" is deleted successfully!!";
    }
}
