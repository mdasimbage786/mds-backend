package com.pvncodes.MD.controllers;

import com.pvncodes.MD.models.Donation;
import com.pvncodes.MD.models.Medicine;
import com.pvncodes.MD.models.User;
import com.pvncodes.MD.services.DonationService;
import com.pvncodes.MD.services.NewMedicineService;
import com.pvncodes.MD.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    @Autowired
    private DonationService donationService;

    @Autowired
    private UserService userService;

    @Autowired
    private NewMedicineService newMedicineService;

    @PostMapping
    public ResponseEntity<Donation> addDonation(@RequestBody DonationRequest donationRequest) {
        Optional<User> user = userService.findById(donationRequest.getUserId());
        Optional<Medicine> medicine = newMedicineService.findById(donationRequest.getMedicineId());

        if (user.isPresent() && medicine.isPresent()) {
            Donation donation = new Donation();
            donation.setDonor(user.get());
            donation.setMedicine(medicine.get());
            donation.setQuantity(donationRequest.getQuantity());
            donation.setDate(donationRequest.getDate());

            return new ResponseEntity<>(donationService.saveDonation(donation), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public List<DonationResponse> getAllDonations() {
        List<Donation> donations = donationService.getAllDonations();
        return donations.stream().map(this::convertToDonationResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonationResponse> getDonationById(@PathVariable Long id) {
        Optional<Donation> donation = donationService.findById(id);
        return donation.map(value -> new ResponseEntity<>(convertToDonationResponse(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable Long id) {
        donationService.deleteDonation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private DonationResponse convertToDonationResponse(Donation donation) {
        DonationResponse response = new DonationResponse();
        response.setDonationId(donation.getId());
        response.setUserId(donation.getDonor().getId());
        response.setUserFirstName(donation.getDonor().getFirstName());
        response.setUserLastName(donation.getDonor().getLastName());
        response.setUserAddress(donation.getDonor().getAddress());
        response.setUserContactNumber(donation.getDonor().getContactNumber());
        response.setMedicineId(donation.getMedicine().getId());
        response.setMedicineName(donation.getMedicine().getName());
        response.setMedicineManufacturer(donation.getMedicine().getManufacturer());
        response.setMedicineExpiryDate(donation.getMedicine().getExpiryDate());
        response.setMedicineDescription(donation.getMedicine().getDescription());
        response.setQuantity(donation.getQuantity());
        response.setDonationDate(donation.getDate());
        return response;
    }
}