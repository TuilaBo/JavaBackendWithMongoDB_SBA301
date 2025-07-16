package repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pojo.Account;

@Repository
public interface AccountRepo extends MongoRepository<Account, String> {
    Account findByEmail(String email);
}
