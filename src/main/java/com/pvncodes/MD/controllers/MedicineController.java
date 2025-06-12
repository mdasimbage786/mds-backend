package com.pvncodes.MD.controllers;

import com.pvncodes.MD.models.Medicine;
import com.pvncodes.MD.services.NewMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "https://preeminent-mochi-42cfb6.netlify.app")
public class MedicineController {
    @Autowired
    private NewMedicineService newMedicineService;

    @PostMapping
    public ResponseEntity<Medicine> addMedicine(@RequestBody Medicine medicine) {
        return new ResponseEntity<>(newMedicineService.saveMedicine(medicine), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Medicine> getAllMedicines() {
        return newMedicineService.getAllMedicines();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable Long id) {
        Optional<Medicine> medicine = newMedicineService.findById(id);
        return medicine.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        newMedicineService.deleteMedicine(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
