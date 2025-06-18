package com.pvncodes.MD.controllers;

import com.pvncodes.MD.models.User;
import com.pvncodes.MD.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "https://preeminent-mochi-42cfb6.netlify.app")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        Optional<User> foundUser = userService.findByEmail(user.getEmail());
        if (foundUser.isPresent() && foundUser.get().getPassword().equals(user.getPassword())) {
            // Include user ID in token for easier extraction
            String token = "fake-jwt-token-" + foundUser.get().getId();
            return ResponseEntity.ok().body(
                    Map.of(
                            "token", token,
                            "userId", foundUser.get().getId(),
                            "firstName", foundUser.get().getFirstName(),
                            "lastName", foundUser.get().getLastName(),
                            "email", foundUser.get().getEmail()
                    )
            );
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    // New endpoint for getting user profile
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from "Bearer token" format
            String token = authHeader.replace("Bearer ", "");

            // Extract user ID from token (assuming format: fake-jwt-token-{userId})
            String userIdStr = token.replace("fake-jwt-token-", "");
            Long userId = Long.parseLong(userIdStr);

            Optional<User> user = userService.findById(userId);
            if (user.isPresent()) {
                // Don't send password in response
                User userResponse = user.get();
                userResponse.setPassword(null);
                return new ResponseEntity<>(userResponse, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    // New endpoint for updating user profile
    @PutMapping("/profile")
    public ResponseEntity<User> updateUserProfile(@RequestHeader("Authorization") String authHeader, @RequestBody User updatedUser) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userIdStr = token.replace("fake-jwt-token-", "");
            Long userId = Long.parseLong(userIdStr);

            Optional<User> existingUser = userService.findById(userId);
            if (existingUser.isPresent()) {
                User user = existingUser.get();

                // Update only non-null fields
                if (updatedUser.getFirstName() != null) user.setFirstName(updatedUser.getFirstName());
                if (updatedUser.getLastName() != null) user.setLastName(updatedUser.getLastName());
                if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
                if (updatedUser.getContactNumber() != null) user.setContactNumber(updatedUser.getContactNumber());
                if (updatedUser.getAddress() != null) user.setAddress(updatedUser.getAddress());

                User savedUser = userService.saveUser(user);
                savedUser.setPassword(null); // Don't send password in response
                return new ResponseEntity<>(savedUser, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}