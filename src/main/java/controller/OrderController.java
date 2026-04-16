package controller;

import dto.common.StatusUpdateRequest;
import dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pojo.Order;
import pojo.OrderDetail;
import service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Operation(summary = "Create new order", description = "Creates a new order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = Order.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody Order order) {
        Order data = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "Order created successfully", data));
    }
    
    @Operation(summary = "Get order by ID", description = "Returns a specific order by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order found",
                    content = @Content(schema = @Schema(implementation = Order.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable String id) {
        Order data = orderService.getOrderById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Order retrieved successfully", data));
    }
    
    @Operation(summary = "Get user orders", description = "Returns all orders for the current user")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved user orders")
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<Order>>> getMyOrders() {
        List<Order> data = orderService.getOrdersByUser();
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Orders retrieved successfully", data));
    }
    
    @Operation(summary = "Get all orders", description = "Returns all orders")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved all orders")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        List<Order> data = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Orders retrieved successfully", data));
    }
    
    @Operation(summary = "Update order status", description = "Updates order status")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(@PathVariable String id, @RequestBody StatusUpdateRequest request) {
        Order data = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Order status updated successfully", data));
    }
    
    @Operation(summary = "Add item to order", description = "Adds an orchid to the current order")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Item added successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/{orderId}/items")
    public ResponseEntity<ApiResponse<OrderDetail>> addOrderItem(@PathVariable String orderId, @RequestBody OrderDetail orderDetail) {
        OrderDetail data = orderService.addOrderItem(orderId, orderDetail);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Item added successfully", data));
    }
}
