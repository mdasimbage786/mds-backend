package com.pvncodes.MD.services;

import com.pvncodes.MD.models.Application;
import com.pvncodes.MD.models.Medicine;
import com.pvncodes.MD.repositories.ApplicationRepository;
import com.pvncodes.MD.repositories.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    public Application saveApplication(Application application) throws IllegalArgumentException {
        // Find medicine by name
        Optional<Medicine> optionalMedicine = medicineRepository.findAll()
                .stream()
                .filter(m -> m.getName().equals(application.getSelectedMedicine()))
                .findFirst();

        if (optionalMedicine.isEmpty()) {
            throw new IllegalArgumentException("Selected medicine not found.");
        }

        Medicine medicine = optionalMedicine.get();

        int requestedQty = application.getQuantity();
        if (requestedQty > medicine.getQuantity()) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock.");
        }

        // Set initial status to "Pending" if not set
        if (application.getStatus() == null || application.getStatus().isEmpty()) {
            application.setStatus("Pending");
        }

        // Update medicine quantity immediately upon application submission
        int newQty = medicine.getQuantity() - requestedQty;
        if (newQty > 0) {
            medicine.setQuantity(newQty);
            medicineRepository.save(medicine);
        } else {
            // Quantity 0 or less, delete medicine
            medicineRepository.delete(medicine);
        }

        // Save application
        return applicationRepository.save(application);
    }

    // Method to approve an application (change status from Pending to Approved)
    public Application approveApplication(Long id) throws IllegalArgumentException {
        Optional<Application> optionalApplication = applicationRepository.findById(id);
        if (optionalApplication.isEmpty()) {
            throw new IllegalArgumentException("Application not found.");
        }

        Application application = optionalApplication.get();
        application.setStatus("Approved");
        return applicationRepository.save(application);
    }

    // Method to mark application as distributed and delete it
    // This simulates the physical distribution completion
    public void distributeApplication(Long id) throws IllegalArgumentException {
        Optional<Application> optionalApplication = applicationRepository.findById(id);
        if (optionalApplication.isEmpty()) {
            throw new IllegalArgumentException("Application not found.");
        }

        Application application = optionalApplication.get();

        // Optional: Log the distribution for tracking
        // You could save to a DistributedApplication entity here for history
        logDistribution(application);

        // Delete the application (as per your current workflow)
        applicationRepository.deleteById(id);
    }

    // Helper method to log distributed applications
    // You can implement this to save to a separate table for tracking
    private void logDistribution(Application application) {
        // TODO: Implement logging to DistributedApplication entity or log file
        // This would help track total distributed count
        System.out.println("Application distributed: " + application.getId() +
                " for " + application.getName()); // Changed from getApplicantName() to getName()
    }

    public void deleteById(Long id) {
        applicationRepository.deleteById(id);
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public List<Application> getPendingApplications() {
        return applicationRepository.findByStatus("Pending");
    }

    public List<Application> getApprovedApplications() {
        return applicationRepository.findByStatus("Approved");
    }
}