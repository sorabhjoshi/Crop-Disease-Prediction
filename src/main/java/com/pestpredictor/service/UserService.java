package com.pestpredictor.service;

import com.pestpredictor.dao.UserDao;
import com.pestpredictor.dao.UserDaoImpl;
import com.pestpredictor.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Service layer for user-related business logic.
 * Handles registration, authentication, and profile management.
 */
@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService() {
        this.userDao = new UserDaoImpl();
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    /**
     * Registers a new user after validating uniqueness of username and email.
     *
     * @throws IllegalArgumentException if username or email already exists
     */
    public User register(String username, String email, String rawPassword, String fullName) {
        if (userDao.existsByUsername(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken.");
        }
        if (userDao.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with email '" + email + "' already exists.");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(username, email, hashedPassword, fullName);
        user.setRole("USER");
        user.setActive(true);

        User saved = userDao.save(user);
        logger.info("New user registered: " + username);
        return saved;
    }

    /**
     * Authenticates a user by username and password.
     *
     * @return Optional<User> — present if credentials are valid, empty otherwise
     */
    public Optional<User> authenticate(String username, String rawPassword) {
        Optional<User> userOpt = userDao.findByUsername(username);

        if (userOpt.isEmpty()) {
            logger.warning("Login attempt for non-existent user: " + username);
            return Optional.empty();
        }

        User user = userOpt.get();

        if (!user.isActive()) {
            logger.warning("Login attempt for deactivated user: " + username);
            return Optional.empty();
        }

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            logger.warning("Incorrect password for user: " + username);
            return Optional.empty();
        }

        userDao.updateLastLogin(user.getId());
        logger.info("User authenticated: " + username);
        return Optional.of(user);
    }

    public Optional<User> findById(Long id) {
        return userDao.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userDao.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }
}
