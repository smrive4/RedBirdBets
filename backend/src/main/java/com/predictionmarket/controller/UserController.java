package com.predictionmarket.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.predictionmarket.dto.LeaderboardEntryDTO;
import com.predictionmarket.model.User;
import com.predictionmarket.service.BetService;
import com.predictionmarket.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private BetService betService;


    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.registerUser(user.getUsername(), user.getPassword(), user.getBalance());
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/login")
    public boolean login(@RequestParam String username, @RequestParam String password) {
        return userService.loginUser(username, password);
    }

    @PostMapping("/login-user")
    public User loginAndGetUser(@RequestParam String username, @RequestParam String password) {
        return userService.loginAndGetUser(username, password);
    }

    @PostMapping("/{id}/promote")
    public User promoteToAdmin(@PathVariable Long id, @RequestParam Long requesterId) {
        User requester = userService.getUserById(requesterId);
        if (!requester.getRole().equals("ADMIN")) {
            throw new IllegalStateException("Only admins can promote users");
        }
        return userService.promoteToAdmin(id);
    }

    @GetMapping("/leaderboard/balance")
    public List<LeaderboardEntryDTO> getLeaderboard(){
        return userService.getLeaderboard().stream().map(user -> new LeaderboardEntryDTO(user.getId(), user.getUsername(), user.getBalance())).collect(Collectors.toList());
    }

    @GetMapping("/leaderboard/winnings")
    public List<LeaderboardEntryDTO> getLeaderboardByWinnings(){
        return betService.getLeaderboardByWinningsDesc();
    }

    @GetMapping("/leaderboard/losses")
    public List<LeaderboardEntryDTO> getLeaderboardByLosses(){
        return betService.getLeaderboardByLossesDesc();
    }
}