package com.pvncodes.MD.controllers;

import com.pvncodes.MD.models.Medicine;
import com.pvncodes.MD.models.PendingDonation;
import com.pvncodes.MD.services.MedicineService;
import com.pvncodes.MD.services.PendingDonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pending-donations")
@CrossOrigin(origins = "https://preeminent-mochi-42cfb6.netlify.app")
public class PendingDonationController {

    @Autowired
    private PendingDonationService pendingDonationService;

    @Autowired
    private MedicineService medicineService;

    // Create with a verification code
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PendingDonation donation) {
        try {
            // Generate 4-digit code
            String code = String.format("%04d", (int)(Math.random() * 10000));
            donation.setVerificationCode(code);

            PendingDonation saved = pendingDonationService.save(donation);

            // Create response map to ensure verification code is included
            Map<String, Object> response = new HashMap<>();
            response.put("id", saved.getId());
            response.put("name", saved.getName());
            response.put("manufacturer", saved.getManufacturer());
            response.put("expiryDate", saved.getExpiryDate());
            response.put("description", saved.getDescription());
            response.put("quantity", saved.getQuantity());
            response.put("address", saved.getAddress());
            response.put("verificationCode", saved.getVerificationCode());

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log the exception for debugging
            System.err.println("Error creating donation: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create donation: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<PendingDonation>> getAll() {
        try {
            List<PendingDonation> donations = pendingDonationService.getAll();
            return new ResponseEntity<>(donations, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error fetching donations: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveDonation(
            @PathVariable Long id,
            @RequestParam String code) {

        try {
            Optional<PendingDonation> optionalDonation = pendingDonationService.getById(id);
            if (!optionalDonation.isPresent()) {
                return new ResponseEntity<>("Donation not found", HttpStatus.NOT_FOUND);
            }

            PendingDonation donation = optionalDonation.get();

            // Check if verification code matches
            if (!donation.getVerificationCode().equals(code)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Invalid verification code");
            }

            // Create new medicine from donation
            Medicine med = new Medicine();
            med.setName(donation.getName());
            med.setManufacturer(donation.getManufacturer());
            med.setExpiryDate(donation.getExpiryDate());
            med.setDescription(donation.getDescription());
            med.setQuantity(donation.getQuantity());
            med.setAddress(donation.getAddress());

            medicineService.saveMedicine(med);
            pendingDonationService.deleteById(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Donation approved successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Error approving donation: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to approve donation: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            pendingDonationService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            System.err.println("Error deleting donation: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete donation: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}