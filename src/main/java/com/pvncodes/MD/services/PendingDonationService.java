package com.pvncodes.MD.services;

import com.pvncodes.MD.models.PendingDonation;
import com.pvncodes.MD.repositories.PendingDonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PendingDonationService {

    private final PendingDonationRepository pendingDonationRepository;

    @Autowired
    public PendingDonationService(PendingDonationRepository pendingDonationRepository) {
        this.pendingDonationRepository = pendingDonationRepository;
    }

    public PendingDonation save(PendingDonation donation) {
        return pendingDonationRepository.save(donation);
    }

    public List<PendingDonation> getAll() {
        return pendingDonationRepository.findAll();
    }

    public Optional<PendingDonation> getById(Long id) {
        return pendingDonationRepository.findById(id);
    }

    public void deleteById(Long id) {
        pendingDonationRepository.deleteById(id);
    }
}
