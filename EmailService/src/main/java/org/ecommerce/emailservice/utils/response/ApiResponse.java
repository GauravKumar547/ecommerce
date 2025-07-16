package org.ecommerce.emailservice.utils.response;

import lombok.Getter;
import lombok.Setter;
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
    private Long timestamp;

    public ApiResponse() {
        this.status = null;
        this.data = null;
        this.error = null;
        this.timestamp = System.currentTimeMillis();
    }

    public static <V> ResponseEntity<ApiResponse<V>> getResponseEntity(ApiResponse<V> apiResponse) {
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    public static <V> ResponseEntity<ApiResponse<V>> ok(V data) {
        ApiResponse<V> response = new ApiResponse<>();
        response.setStatus(HttpStatus.OK).setData(data);
        return getResponseEntity(response);
    }

    public static <V> ResponseEntity<ApiResponse<V>> created(V data) {
        ApiResponse<V> response = new ApiResponse<>();
        response.setStatus(HttpStatus.CREATED).setData(data);
        return getResponseEntity(response);
    }

    public static <V> ResponseEntity<ApiResponse<V>> error(HttpStatus status, String error) {
        ApiResponse<V> response = new ApiResponse<>();
        response.setStatus(status).setError(error);
        return getResponseEntity(response);
    }
} 