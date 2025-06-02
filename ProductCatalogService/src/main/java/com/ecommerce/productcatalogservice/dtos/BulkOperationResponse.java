package com.ecommerce.productcatalogservice.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BulkOperationResponse<T> {
    private int totalCount;
    private int successCount;
    private int failureCount;
    private List<T> successItems = new ArrayList<>();
    private List<FailureItem> failureItems = new ArrayList<>();

    @Data
    public static class FailureItem {
        private String id;
        private String reason;
    }
} 