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
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalMedicines = medicineRepository.count();
        long totalDonations = applicationRepository.count();
        long pendingApplications = applicationRepository.countByStatus("Pending");
        long approvedApplications = applicationRepository.countByStatus("Approved");

        stats.put("totalMedicines", totalMedicines);
        stats.put("totalDonations", totalDonations);
        stats.put("pendingApplications", pendingApplications);
        stats.put("approvedApplications", approvedApplications);

        return stats;
    }
}
