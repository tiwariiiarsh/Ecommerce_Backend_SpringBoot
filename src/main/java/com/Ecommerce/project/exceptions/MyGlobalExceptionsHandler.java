package com.Ecommerce.project.exceptions;


import com.Ecommerce.project.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
//@RestControllerAdvice ka use karne se aap poore application me centralized error handling aur consistent API response maintain kar sakte ho.
public class MyGlobalExceptionsHandler {
//    @ExceptionHandler(Class which handle) → specific exception ko handle karta hai.
//    @RestControllerAdvice → globally saare controllers ke liye exception handle karne ki jagah provide karta hai.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String,String>response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err -> {
            String fieldName =((FieldError)err).getField();
//            getting default error message
            String message = err.getDefaultMessage();
            response.put(fieldName,message);
        });
        return  new ResponseEntity<Map<String,String>>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse>myResourceNotFoundException(ResourceNotFoundException e){
        String message =e.getMessage();
        APIResponse apiResponse = new APIResponse(message,false);
                return new ResponseEntity<>(apiResponse,HttpStatus.NOT_FOUND);
    }



    @ExceptionHandler(ApiException.class)
    public ResponseEntity<APIResponse>ApiException(ApiException e){
        String message =e.getMessage();
        APIResponse apiResponse = new APIResponse(message,false);
        return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
    }


}
