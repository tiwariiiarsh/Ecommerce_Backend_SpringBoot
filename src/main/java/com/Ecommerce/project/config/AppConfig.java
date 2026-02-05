package com.Ecommerce.project.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//This method of creating an object is custom Method and
// we will use this method only when external library are used
//@Configuration → Class jisme beans banate ho.
//@Bean → Method jo bean return karta hai.


//@Autowired → Bean ko inject karke use karna.

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}


//----------------NOTES----------------------------
//
//🔹 Why we use ModelMapper?
//Jab hum Spring Boot me DTO (Data Transfer Object) aur Entity classes banate hain,
// unke fields mostly same hote hain. Lekin unhe manually map karna time-consuming aur boring kaam hota hai.
//Example without ModelMapper 👇
//UserDTO dto = new UserDTO();
//dto.setId(user.getId());
//        dto.setName(user.getName());
//        dto.setEmail(user.getEmail());
//
//        👉 Har field manually copy karni padti hai. Agar class me 20 fields ho toh pura din copy-paste hi karte rahoge 😅
//        🔹 ModelMapper ka kaam
//ModelMapper ek library hai jo yeh mapping automatically kar deta hai. Tumhe bas source aur destination class batani hai, baaki vo fields ko match karke map kar dega.
//Example with ModelMapper 👇
//ModelMapper modelMapper = new ModelMapper();
//UserDTO dto = modelMapper.map(user, UserDTO.class);
//👉 Bas ek line me pura conversion done ✅