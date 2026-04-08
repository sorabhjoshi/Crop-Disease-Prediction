package com.pestpredictor.dao;

import com.pestpredictor.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for User operations.
 * Implemented by UserDaoImpl using JDBC + Hibernate.
 */
public interface UserDao {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User update(User user);

    void delete(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void updateLastLogin(Long userId);
}
