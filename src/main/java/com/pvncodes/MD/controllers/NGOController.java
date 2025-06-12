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

        // ✅ Include name and address in the response
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("name", dbNgo.getName());
        response.put("address", dbNgo.getAddress());

        return ResponseEntity.ok(response);
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
