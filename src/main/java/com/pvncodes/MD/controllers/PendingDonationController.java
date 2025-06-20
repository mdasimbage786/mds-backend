package com.pvncodes.MD.controllers;

import com.pvncodes.MD.models.Medicine;
import com.pvncodes.MD.models.PendingDonation;
import com.pvncodes.MD.services.MedicineService;
import com.pvncodes.MD.services.PendingDonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<PendingDonation> create(@RequestBody PendingDonation donation) {
        // Generate 4-digit code
        String code = String.format("%04d", (int)(Math.random() * 10000));
        donation.setVerificationCode(code);

        PendingDonation saved = pendingDonationService.save(donation);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public List<PendingDonation> getAll() {
        return pendingDonationService.getAll();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveDonation(
            @PathVariable Long id,
            @RequestParam String code) {

        Optional<PendingDonation> optionalDonation = pendingDonationService.getById(id);
        if (!optionalDonation.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PendingDonation donation = optionalDonation.get();

        if (!donation.getVerificationCode().equals(code)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Invalid verification code");
        }

        Medicine med = new Medicine();
        med.setName(donation.getName());
        med.setManufacturer(donation.getManufacturer());
        med.setExpiryDate(donation.getExpiryDate());
        med.setDescription(donation.getDescription());
        med.setQuantity(donation.getQuantity());
        med.setAddress(donation.getAddress());

        medicineService.saveMedicine(med);
        pendingDonationService.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pendingDonationService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
