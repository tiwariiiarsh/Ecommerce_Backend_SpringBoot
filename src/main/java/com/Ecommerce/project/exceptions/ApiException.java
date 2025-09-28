package com.Ecommerce.project.exceptions;

public class ApiException extends RuntimeException{
    private  static  final  long serialVersionUID = 1L;

    public ApiException() {
    }

    public ApiException(String message) {
        super(message);
    }
}
//---------------------------NOTE-----------------------------------------
//-------------private static final long serialVersionUID = 1L;---------------------
//Ye ek ID hai jo Java ko batata hai class ka version.
//Matlab tumhari class ka ek unique number hota hai.
//Jab tum object ko save karte ho (serialize) aur baad me load karte ho (deserialize), Java check karta hai ki class ka version same hai ya nahi.
//Agar version alag hoga to Java bolega → "Ye class badal gayi hai, purana data samajh nahi aayega" → error aayega.
//Isliye hum khud serialVersionUID = 1L; de dete hain, taaki Java hamesha isko same samjhe.