package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pojo.Account;

@Repository
public interface AccountRepo extends JpaRepository<Account, String> {
    Account findByEmail(String email);
}
