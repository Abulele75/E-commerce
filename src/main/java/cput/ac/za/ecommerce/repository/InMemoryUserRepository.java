package cput.ac.za.ecommerce.repository;

import cput.ac.za.ecommerce.domain.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
/*
   InMemoryUserRepository.java
   Owenkosi Nxasana (230240887)
   Date: 12 July 2026
 */
@Repository
public final class InMemoryUserRepository implements UserManagementRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getUserId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getAccountProfile().getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(String userId) {
        users.remove(userId);
    }
}
