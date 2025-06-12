package com.pvncodes.MD.services;

import com.pvncodes.MD.models.Medicine;
import com.pvncodes.MD.repositories.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicineService {
    private final MedicineRepository medicineRepository;

    @Autowired
    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public Medicine saveMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public Optional<Medicine> findById(Long id) {
        return medicineRepository.findById(id);
    }

    public void deleteMedicine(Long id) {
        medicineRepository.deleteById(id);
    }
}