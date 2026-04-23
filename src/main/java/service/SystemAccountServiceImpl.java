package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pojo.Account;
import pojo.Role;
import repository.AccountRepo;
import repository.RoleRepository;

import java.util.Collections;
import java.util.UUID;

@Service
@Primary
public class SystemAccountServiceImpl implements SystemAccountService {
    private static final Logger logger = LoggerFactory.getLogger(SystemAccountServiceImpl.class);

    private final AccountRepo accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public SystemAccountServiceImpl(AccountRepo accountRepo, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.accountRepo = accountRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Loading user by username: {}", email);
        Account account = accountRepo.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        String roleName = "USER";
        if (account.getRoleId() != null) {
            Role role = roleRepository.findById(account.getRoleId()).orElse(null);
            if (role != null) {
                roleName = role.getRoleName();
            }
        }
        String authority = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;

        return new User(
                account.getEmail(),
                account.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(authority))
        );
    }

    @Override
    public boolean validateUser(String email, String password) {
        Account account = accountRepo.findByEmail(email);
        return account != null && passwordEncoder.matches(password, account.getPassword());
    }

    @Override
    public Account findByEmail(String email) {
        return accountRepo.findByEmail(email);
    }

    @Override
    public Account createUser(String email, String password, String roleId, String accountName) {
        Account account = new Account();
        account.setId(UUID.randomUUID().toString());
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode(password));
        account.setRoleId(roleId);
        account.setAccountName(accountName);
        return accountRepo.save(account);
    }

    @Override
    public Account updateUserRole(String userId, String roleId) {
        Account account = accountRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        account.setRoleId(roleId);
        return accountRepo.save(account);
    }
}
