package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pojo.Order;
import pojo.OrderDetail;
import pojo.Account;
import pojo.OrderStatus;
import repository.OrderRepository;
import repository.AccountRepo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccountRepo accountRepo;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order createOrder(Order order) {
        // Set current user as order owner
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Account account = accountRepo.findByEmail(email);
        if (account == null) {
            throw new IllegalArgumentException("User not found");
        }
        order.setAccountId(account.getId());
        order.setOrderDate(new Date());
        order.setOrderStatus("PENDING");
        order.setTotalAmount(BigDecimal.ZERO);
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrderStatus(String id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Account account = accountRepo.findByEmail(email);
        if (account == null) {
            throw new IllegalArgumentException("User not found");
        }
        return orderRepository.findByAccountId(account.getId());
    }

    @Override
    public OrderDetail addOrderItem(String orderId, OrderDetail orderDetail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        orderDetail.setOrderId(orderId);
        // Calculate and update total amount
        BigDecimal currentTotal = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal itemTotal = BigDecimal.valueOf(orderDetail.getPrice() * orderDetail.getQuantity());
        order.setTotalAmount(currentTotal.add(itemTotal));
        orderRepository.save(order);
        return orderDetail;
    }

    @Override
    public boolean isOrderOwner(String orderId, String username) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            return order.getAccountId().equals(username);
        }
        return false;
    }
} 