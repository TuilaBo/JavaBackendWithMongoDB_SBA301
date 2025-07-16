package service;

import pojo.Order;
import pojo.OrderDetail;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();
    Optional<Order> getOrderById(String id);
    Order createOrder(Order order);
    Order updateOrderStatus(String id, String status);
    List<Order> getOrdersByUser();
    OrderDetail addOrderItem(String orderId, OrderDetail orderDetail);
    boolean isOrderOwner(String orderId, String username);
} 