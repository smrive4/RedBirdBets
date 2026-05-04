package com.predictionmarket.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.predictionmarket.dto.UserResponse;
import com.predictionmarket.model.User;
import com.predictionmarket.repository.UserRepository;
import com.predictionmarket.security.JwtUtil;

/* Service class for handling user-related operations */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // Register a new user with a username, password, and initial balance
    public User registerUser(String username, String password, BigDecimal balance) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setBalance(balance);
        user.setRole("USER");
        return userRepository.save(user);
    }

    // Authenticate a user by verifying the provided username and password
    public boolean loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) return false;
        return passwordEncoder.matches(password, user.getPassword());
    }

    // Authenticate a user and return a UserResponse containing user details and a JWT token if authentication is successful
    public UserResponse loginAndGetUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }
        if (!passwordEncoder.matches(password, user.getPassword())) return null;
        return new UserResponse(user.getId(), user.getUsername(), user.getRole(), user.getBalance(), jwtUtil.createToken(user.getUsername(), user.getRole()));
    }

    // Retrieve a user by their ID, throwing an exception if the user is not found
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // Promote a user to an admin role by updating their role and saving the changes to the database
    public User promoteToAdmin(Long id) {
        User user = getUserById(id);
        user.setRole("ADMIN");
        return userRepository.save(user);
    }

    // Retrieve a list of users ordered by their balance in descending order for leaderboard purposes
    public List<User> getLeaderboard() {
        return userRepository.findAllOrderByBalanceDesc();
    }

    // Allow a user to claim a daily reward, ensuring that they can only claim once per day and updating their balance accordingly
    public User claimDailyReward(long id){
        User user = getUserById(id);
        if (user.getLastDailyClaimDate() != null && user.getLastDailyClaimDate().isEqual(java.time.LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Daily reward already claimed");
        }
        user.setBalance(user.getBalance().add(BigDecimal.valueOf(250)));
        user.setLastDailyClaimDate(java.time.LocalDate.now());
        return userRepository.save(user);
    }
}