package repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pojo.Orchid;
import java.util.List;

public interface OrchidRepository extends MongoRepository<pojo.Orchid, String> {
    Orchid findByOrchidName(String orchidName);
    java.util.List<pojo.Orchid> findByCategoryId(String categoryId);
    java.util.List<pojo.Orchid> findByOrchidNameContainingIgnoreCase(String name);
}
