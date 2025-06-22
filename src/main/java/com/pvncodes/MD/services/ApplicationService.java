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

    // Method to get application by ID
    public Optional<Application> getById(Long id) {
        return applicationRepository.findById(id);
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

    // Method to reject an application (change status from Pending to Rejected)
    public Application rejectApplication(Long id) throws IllegalArgumentException {
        Optional<Application> optionalApplication = applicationRepository.findById(id);
        if (optionalApplication.isEmpty()) {
            throw new IllegalArgumentException("Application not found.");
        }

        Application application = optionalApplication.get();
        
        // Restore medicine quantity when application is rejected
        restoreMedicineQuantity(application);
        
        application.setStatus("Rejected");
        return applicationRepository.save(application);
    }

    // Helper method to restore medicine quantity
    private void restoreMedicineQuantity(Application application) {
        try {
            // Find existing medicine by name
            Optional<Medicine> optionalMedicine = medicineRepository.findAll()
                    .stream()
                    .filter(m -> m.getName().equals(application.getSelectedMedicine()))
                    .findFirst();

            if (optionalMedicine.isPresent()) {
                // Medicine exists, restore quantity
                Medicine medicine = optionalMedicine.get();
                medicine.setQuantity(medicine.getQuantity() + application.getQuantity());
                medicineRepository.save(medicine);
                System.out.println("Restored " + application.getQuantity() + " units of " + 
                                 application.getSelectedMedicine() + " due to application rejection.");
            } else {
                // Medicine doesn't exist (was deleted when quantity reached 0)
                // Create new medicine entry with the rejected quantity
                Medicine newMedicine = new Medicine();
                newMedicine.setName(application.getSelectedMedicine());
                newMedicine.setQuantity(application.getQuantity());
                // Set default values for required fields
                newMedicine.setManufacturer("Unknown");
                newMedicine.setDescription("Restored from rejected application");
                newMedicine.setAddress("Contact admin for details");
                // You might want to set a default expiry date or handle this differently
                medicineRepository.save(newMedicine);
                System.out.println("Created new medicine entry for " + application.getSelectedMedicine() + 
                                 " with quantity " + application.getQuantity() + " due to application rejection.");
            }
        } catch (Exception e) {
            System.err.println("Error restoring medicine quantity for application " + application.getId() + 
                             ": " + e.getMessage());
            // Log error but don't throw exception to avoid blocking the rejection process
        }
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

    public List<Application> getRejectedApplications() {
        return applicationRepository.findByStatus("Rejected");
    }
}
