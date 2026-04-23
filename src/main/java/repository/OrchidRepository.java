package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pojo.Orchid;
import java.util.List;

public interface OrchidRepository extends JpaRepository<Orchid, String> {
    Orchid findByOrchidName(String orchidName);
    List<Orchid> findByCategoryId(Long categoryId);
    List<Orchid> findByOrchidNameContainingIgnoreCase(String name);
}
