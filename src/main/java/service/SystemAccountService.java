package service;

import org.springframework.security.core.userdetails.UserDetailsService;
import pojo.Account;

public interface SystemAccountService extends UserDetailsService {
    boolean validateUser(String email, String password);
    Account findByEmail(String email);
    Account createUser(String email, String password, String roleId, String accountName);
    Account updateUserRole(String userId, String roleId);
}
