package com.pvncodes.MD.repositories;

import com.pvncodes.MD.models.PendingDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingDonationRepository extends JpaRepository<PendingDonation, Long> {}
