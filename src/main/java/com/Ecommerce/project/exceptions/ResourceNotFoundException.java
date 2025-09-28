package com.Ecommerce.project.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    String resourceName;
    String fieldName;
    String field;
    Long fieldId;

    public ResourceNotFoundException(String field,String resourceName, String fieldName) {
        super(String.format("%s not found with %s:%s",resourceName,field,fieldName));
        this.field = field;
        this.fieldName=fieldName;
        this.resourceName=resourceName;
    }

    public ResourceNotFoundException(String field,String resourceName, Long fieldId) {
        super(String.format("%s not found with %s:%d",resourceName,field,fieldId));
        this.field = field;
       this.fieldId = fieldId;
       this.resourceName=resourceName;
    }
}
