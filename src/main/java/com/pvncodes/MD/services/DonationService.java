package com.pvncodes.MD.services;

import com.pvncodes.MD.models.Donation;
import com.pvncodes.MD.repositories.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonationService {
    @Autowired
    private DonationRepository donationRepository;

    public Donation saveDonation(Donation donation) {
        return donationRepository.save(donation);
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    public Optional<Donation> findById(Long id) {
        return donationRepository.findById(id);
    }

    public void deleteDonation(Long id) {
        donationRepository.deleteById(id);
    }
}
