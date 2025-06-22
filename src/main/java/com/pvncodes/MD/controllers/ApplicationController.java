package com.pvncodes.MD.controllers;

import com.pvncodes.MD.models.Application;
import com.pvncodes.MD.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "https://preeminent-mochi-42cfb6.netlify.app")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<?> submitApplication(@RequestBody Application application) {
        try {
            // Generate 4-digit verification code
            String code = String.format("%04d", (int)(Math.random() * 10000));
            application.setVerificationCode(code);
            
            Application savedApp = applicationService.saveApplication(application);
            
            // Create response map to ensure verification code is included
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedApp.getId());
            response.put("selectedMedicine", savedApp.getSelectedMedicine());
            response.put("quantity", savedApp.getQuantity());
            response.put("ngo", savedApp.getNgo());
            response.put("name", savedApp.getName());
            response.put("mobile", savedApp.getMobile());
            response.put("address", savedApp.getAddress());
            response.put("status", savedApp.getStatus());
            response.put("verificationCode", savedApp.getVerificationCode());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.err.println("Error creating application: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create application: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<List<Application>> getAllApplications() {
        try {
            List<Application> applications = applicationService.getAllApplications();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            System.err.println("Error fetching applications: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveApplication(
            @PathVariable Long id,
            @RequestParam String code) {
        
        try {
            Optional<Application> optionalApplication = applicationService.getById(id);
            if (!optionalApplication.isPresent()) {
                return new ResponseEntity<>("Application not found", HttpStatus.NOT_FOUND);
            }

            Application application = optionalApplication.get();

            // Check if verification code matches
            if (!application.getVerificationCode().equals(code)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Invalid verification code");
            }

            // Update application status to approved
            application.setStatus("Approved");
            applicationService.saveApplication(application);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Application approved successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Error approving application: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to approve application: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectApplication(
            @PathVariable Long id,
            @RequestParam String code) {
        
        try {
            Optional<Application> optionalApplication = applicationService.getById(id);
            if (!optionalApplication.isPresent()) {
                return new ResponseEntity<>("Application not found", HttpStatus.NOT_FOUND);
            }

            Application application = optionalApplication.get();

            // Check if verification code matches
            if (!application.getVerificationCode().equals(code)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Invalid verification code");
            }

            // Update application status to rejected
            application.setStatus("Rejected");
            applicationService.saveApplication(application);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Application rejected successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Error rejecting application: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to reject application: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        try {
            applicationService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            System.err.println("Error deleting application: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
                }
