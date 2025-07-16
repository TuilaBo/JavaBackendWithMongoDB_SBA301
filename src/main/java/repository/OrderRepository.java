package repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pojo.Order;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByAccountId(String accountId);
}
