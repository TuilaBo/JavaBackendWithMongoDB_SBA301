package service;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.Account;
import pojo.Role;
import repository.AccountRepo;
import repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

@Service
@Primary
public class SystemAccountService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(SystemAccountService.class);

    private final AccountRepo accountRepo;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    public SystemAccountService(AccountRepo accountRepo, PasswordEncoder passwordEncoder) {
        this.accountRepo = accountRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Loading user by username: {}", email);
        Account account = accountRepo.findByEmail(email);
        if (account == null) {
            logger.warn("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Lấy roleName từ roleId
        String roleName = "USER";
        if (account.getRoleId() != null) {
            Role role = roleRepository.findById(account.getRoleId()).orElse(null);
            if (role != null) {
                roleName = role.getRoleName();
            }
        }
        String authority = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;

        UserDetails userDetails = new User(
                account.getEmail(),
                account.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(authority))
        );
        return userDetails;
    }

    public boolean validateUser(String email, String password) {
        logger.info("Validating user with email: {}", email);
        Account account = accountRepo.findByEmail(email);
        if (account == null) {
            logger.warn("No matching account found for email: {}", email);
            return false;
        }
        boolean matches = passwordEncoder.matches(password, account.getPassword());
        if (!matches) {
            logger.warn("Password does not match for email: {}", email);
        }
        return matches;
    }

    public Account findByEmail(String email) {
        return accountRepo.findByEmail(email);
    }

    // Method để tạo user mới với password được mã hóa
    public Account createUser(String email, String password, String roleId, String accountName) {
        Account account = new Account();
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode(password));
        account.setRoleId(roleId);
        account.setAccountName(accountName);
        return accountRepo.save(account);
    }

    // Method để cập nhật role của user
    public Account updateUserRole(String userId, String roleId) {
        Account account = accountRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        account.setRoleId(roleId);
        return accountRepo.save(account);
    }
}

