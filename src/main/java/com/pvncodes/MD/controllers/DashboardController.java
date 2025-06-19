package com.pvncodes.MD.controllers;

import com.pvncodes.MD.repositories.ApplicationRepository;
import com.pvncodes.MD.repositories.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "https://preeminent-mochi-42cfb6.netlify.app")
public class DashboardController {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Available medicines in inventory (medicines with quantity > 0)
        long availableMedicines = medicineRepository.count();

        // Total quantity of all available medicines
        long totalMedicineQuantity = medicineRepository.findAll()
                .stream()
                .mapToLong(medicine -> medicine.getQuantity())
                .sum();

        // New medicine donations (medicines added in last 7 days or by status)
        // For now, showing total medicines as "donations received"
        long pendingDonations = availableMedicines; // All current medicines are donations

        // Pending applications (waiting for physical distribution)
        long pendingDistributions = applicationRepository.countByStatus("Pending");

        // Applications that have been approved/allocated (medicine quantity already reduced)
        long approvedApplications = applicationRepository.countByStatus("Approved");

        // Total applications ever submitted (including deleted ones would need separate tracking)
        long totalApplications = applicationRepository.count();

        stats.put("availableMedicines", availableMedicines);
        stats.put("totalMedicineQuantity", totalMedicineQuantity);
        stats.put("pendingDonations", pendingDonations);
        stats.put("pendingDistributions", pendingDistributions);
        stats.put("approvedApplications", approvedApplications);
        stats.put("totalApplications", totalApplications);

        return stats;
    }
}