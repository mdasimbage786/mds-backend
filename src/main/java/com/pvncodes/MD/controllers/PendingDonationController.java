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


    @PostMapping
    public ResponseEntity<PendingDonation> create(@RequestBody PendingDonation donation) {
        return new ResponseEntity<>(pendingDonationService.save(donation), HttpStatus.CREATED);
    }

    @GetMapping
    public List<PendingDonation> getAll() {
        return pendingDonationService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PendingDonation> getById(@PathVariable Long id) {
        Optional<PendingDonation> donation = pendingDonationService.getById(id);
        return donation.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ✅ FIXED: This method now matches the frontend's request URL
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveDonation(@PathVariable Long id) {
        Optional<PendingDonation> optionalDonation = pendingDonationService.getById(id);
        if (!optionalDonation.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PendingDonation donation = optionalDonation.get();

        // Convert to Medicine entity (you must create a constructor or builder for this)
        Medicine medicine = new Medicine();
        medicine.setName(donation.getName());
        medicine.setManufacturer(donation.getManufacturer());
        medicine.setExpiryDate(donation.getExpiryDate());
        medicine.setDescription(donation.getDescription());
        medicine.setQuantity(donation.getQuantity());
        medicine.setAddress(donation.getAddress());

        // Save to Medicine table
        medicineService.saveMedicine(medicine);


        // Delete from pending donations
        pendingDonationService.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pendingDonationService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
