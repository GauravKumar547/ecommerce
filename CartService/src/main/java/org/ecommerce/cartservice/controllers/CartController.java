package org.ecommerce.cartservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.ecommerce.cartservice.dtos.AddItemRequest;
import org.ecommerce.cartservice.dtos.ApiResponse;
import org.ecommerce.cartservice.dtos.CartResponse;
import org.ecommerce.cartservice.dtos.UpdateItemRequest;
import org.ecommerce.cartservice.services.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Shopping Cart Management", description = "APIs for managing shopping carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get or create cart", description = "Get active cart for user or create new one if none exists")
    public ResponseEntity<ApiResponse<CartResponse>> getOrCreateCart(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        ApiResponse<CartResponse> apiResponse = new ApiResponse<>();
        CartResponse response = cartService.getOrCreateCart(userId);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @GetMapping("/{cartId}")
    @Operation(summary = "Get cart by ID", description = "Get cart details by cart ID")
    public ResponseEntity<ApiResponse<CartResponse>> getCartById(
            @Parameter(description = "Cart ID") @PathVariable Long cartId) {
        ApiResponse<CartResponse> apiResponse = new ApiResponse<>();
        CartResponse response = cartService.getCartById(cartId);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/user/{userId}/items")
    @Operation(summary = "Add item to cart", description = "Add a new item to user's cart")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Valid @RequestBody AddItemRequest request) {
        ApiResponse<CartResponse> apiResponse = new ApiResponse<>();
        CartResponse response = cartService.addItem(userId, request);
        apiResponse.setData(response).setStatus(HttpStatus.CREATED);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PutMapping("/user/{userId}/items/{itemId}")
    @Operation(summary = "Update cart item", description = "Update quantity or discount of a cart item")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Item ID") @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemRequest request) {
        ApiResponse<CartResponse> apiResponse = new ApiResponse<>();
        CartResponse response = cartService.updateItem(userId, itemId, request);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @DeleteMapping("/user/{userId}/items/{itemId}")
    @Operation(summary = "Remove cart item", description = "Remove an item from the cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Item ID") @PathVariable Long itemId) {
        ApiResponse<CartResponse> apiResponse = new ApiResponse<>();
        CartResponse response = cartService.removeItem(userId, itemId);
        apiResponse.setData(response).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/user/{userId}/clear")
    @Operation(summary = "Clear cart", description = "Remove all items from the cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        cartService.clearCart(userId);
        apiResponse.setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/{cartId}/deactivate")
    @Operation(summary = "Deactivate cart", description = "Deactivate a cart")
    public ResponseEntity<ApiResponse<Void>> deactivateCart(
            @Parameter(description = "Cart ID") @PathVariable Long cartId) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        cartService.deactivateCart(cartId);
        apiResponse.setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @PostMapping("/process-expired")
    @Operation(summary = "Process expired carts", description = "Process and deactivate expired carts")
    public ResponseEntity<ApiResponse<Void>> processExpiredCarts() {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        cartService.processExpiredCarts();
        apiResponse.setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Get active cart count", description = "Get count of active carts for a user")
    public ResponseEntity<ApiResponse<Long>> getActiveCartCount(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        ApiResponse<Long> apiResponse = new ApiResponse<>();
        Long count = cartService.getActiveCartCount(userId);
        apiResponse.setData(count).setStatus(HttpStatus.OK);
        return ApiResponse.getResponseEntity(apiResponse);
    }
} 