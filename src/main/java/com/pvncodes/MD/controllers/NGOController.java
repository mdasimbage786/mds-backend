package com.pvncodes.MD.controllers;

import com.pvncodes.MD.models.NGO;
import com.pvncodes.MD.services.NGOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pvncodes.MD.security.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@RestController
@RequestMapping("/api/ngos")
@CrossOrigin(origins = "https://preeminent-mochi-42cfb6.netlify.app")
public class NGOController {

    @Autowired
    private NGOService ngoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<NGO> registerNGO(@RequestBody NGO ngo) {
        if (ngo.getPassword() == null || ngo.getPassword().length() < 6) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (ngo.getEmail() != null && ngoService.findByEmail(ngo.getEmail()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        ngo.setPassword(passwordEncoder.encode(ngo.getPassword()));
        return new ResponseEntity<>(ngoService.saveNGO(ngo), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginNGO(@RequestBody NGO ngo) {
        Optional<NGO> foundNGO = ngoService.findByEmail(ngo.getEmail());

        if (foundNGO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NGO not found with this email");
        }

        NGO dbNgo = foundNGO.get();

        if (ngo.getPassword() == null || !passwordMatches(dbNgo, ngo.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
        }

        if (ngo.getNgoId() != null && !ngo.getNgoId().equals(dbNgo.getNgoId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid NGO ID");
        }

        String token = jwtService.generateToken(dbNgo.getId(), "NGO");

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("ngoId", dbNgo.getNgoId());
        response.put("name", dbNgo.getName());
        response.put("email", dbNgo.getEmail());
        response.put("address", dbNgo.getAddress());
        response.put("contactNumber", dbNgo.getContactNumber());

        return ResponseEntity.ok(response);
    }

    // New endpoint for getting NGO profile
    @GetMapping("/profile")
    public ResponseEntity<NGO> getNGOProfile(Authentication authentication) {
        try {
            Long ngoId = (Long) authentication.getPrincipal();

            Optional<NGO> ngo = ngoService.findById(ngoId);
            if (ngo.isPresent()) {
                NGO ngoResponse = ngo.get();
                ngoResponse.setPassword(null); // Don't send password in response
                return new ResponseEntity<>(ngoResponse, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    // New endpoint for updating NGO profile
    @PutMapping("/profile")
    public ResponseEntity<NGO> updateNGOProfile(Authentication authentication, @RequestBody NGO updatedNGO) {
        try {
            Long ngoId = (Long) authentication.getPrincipal();

            Optional<NGO> existingNGO = ngoService.findById(ngoId);
            if (existingNGO.isPresent()) {
                NGO ngo = existingNGO.get();

                // Update only non-null fields
                if (updatedNGO.getName() != null) ngo.setName(updatedNGO.getName());
                if (updatedNGO.getEmail() != null) ngo.setEmail(updatedNGO.getEmail());
                if (updatedNGO.getContactNumber() != null) ngo.setContactNumber(updatedNGO.getContactNumber());
                if (updatedNGO.getAddress() != null) ngo.setAddress(updatedNGO.getAddress());
                if (updatedNGO.getNgoId() != null) ngo.setNgoId(updatedNGO.getNgoId());

                NGO savedNGO = ngoService.saveNGO(ngo);
                savedNGO.setPassword(null); // Don't send password in response
                return new ResponseEntity<>(savedNGO, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping
    public List<NGO> getAllNGOs() {
        return ngoService.findAllNGOs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NGO> getNGOById(@PathVariable Long id) {
        Optional<NGO> ngo = ngoService.findById(id);
        return ngo.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNGO(@PathVariable Long id) {
        ngoService.deleteNGO(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /** BCrypt check; transparently upgrades legacy plaintext passwords to BCrypt on first successful login. */
    private boolean passwordMatches(NGO stored, String raw) {
        String saved = stored.getPassword();
        if (saved == null) return false;
        if (saved.startsWith("$2")) {
            return passwordEncoder.matches(raw, saved);
        }
        if (saved.equals(raw)) {
            stored.setPassword(passwordEncoder.encode(raw));
            ngoService.saveNGO(stored);
            return true;
        }
        return false;
    }
}
