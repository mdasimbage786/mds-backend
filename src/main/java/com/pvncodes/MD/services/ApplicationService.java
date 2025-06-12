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
        // Find medicine by name (you may want to optimize by using ID instead)
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

        // Update medicine quantity
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

    public void deleteById(Long id) {
        applicationRepository.deleteById(id);
    }
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }
}