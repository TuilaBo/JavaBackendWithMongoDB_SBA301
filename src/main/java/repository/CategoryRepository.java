package repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pojo.Category;

public interface CategoryRepository extends MongoRepository<pojo.Category, String> {
    pojo.Category findByCategoryName(String categoryName);
}
