package com.pvncodes.MD.controllers;

import com.pvncodes.MD.models.NGO;
import com.pvncodes.MD.services.NGOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ngos")
@CrossOrigin(origins = "https://preeminent-mochi-42cfb6.netlify.app")
public class NGOController {

    @Autowired
    private NGOService ngoService;

    @PostMapping("/register")
    public ResponseEntity<NGO> registerNGO(@RequestBody NGO ngo) {
        return new ResponseEntity<>(ngoService.saveNGO(ngo), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginNGO(@RequestBody NGO ngo) {
        Optional<NGO> foundNGO = ngoService.findByEmail(ngo.getEmail());

        if (foundNGO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("NGO not found with this email");
        }

        NGO dbNgo = foundNGO.get();

        if (!dbNgo.getPassword().equals(ngo.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
        }

        String token = "ngo-token-" + dbNgo.getId();

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("ngoId", dbNgo.getId());
        response.put("name", dbNgo.getName());
        response.put("email", dbNgo.getEmail());
        response.put("address", dbNgo.getAddress());
        response.put("contactNumber", dbNgo.getContactNumber());

        return ResponseEntity.ok(response);
    }

    // New endpoint for getting NGO profile
    @GetMapping("/profile")
    public ResponseEntity<NGO> getNGOProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String ngoIdStr = token.replace("ngo-token-", "");
            Long ngoId = Long.parseLong(ngoIdStr);

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
    public ResponseEntity<NGO> updateNGOProfile(@RequestHeader("Authorization") String authHeader, @RequestBody NGO updatedNGO) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String ngoIdStr = token.replace("ngo-token-", "");
            Long ngoId = Long.parseLong(ngoIdStr);

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
}