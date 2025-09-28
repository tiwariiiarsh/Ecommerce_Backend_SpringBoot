package com.Ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//-----------------------NOTES-------------------------------
//APIResponse ek custom wrapper hai jo har API ka output ek jaisa banata hai
// (message + success + data, etc.). Isse frontend ko response samajhna aur handle karna easy ho jaata hai. ✅

//this is used in MyGlobalExceptionHandler

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse {
    public  String message;
    private boolean status;
}
