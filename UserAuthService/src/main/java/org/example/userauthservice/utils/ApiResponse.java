package org.example.userauthservice.utils;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public static <V> ResponseEntity<ApiResponse<V>> getResponseEntity(ApiResponse<V> response){
        return ResponseEntity.status(response.status).body(response);
    }
}