package com.ecommerce.productcatalogservice.utils.response;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@Accessors(chain = true)
public class ApiResponse<T> {
    private HttpStatus status;
    private T data;
    private String error;

    public ApiResponse() {
        this.status = null;
        this.data = null;
        this.error = null;
    }

    public ResponseEntity<ApiResponse<T>> getResponseEntity(){
        return ResponseEntity.status(status).body(this);
    }

}