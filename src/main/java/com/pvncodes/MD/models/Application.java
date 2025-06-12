package com.pvncodes.MD.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String selectedMedicine;
    private int quantity;
    private String ngo;
    private String name;
    private String mobile;
    private String address;

    // Constructors
    public Application() {
    }

    public Application(Long id, String selectedMedicine, int quantity, String ngo, String name, String mobile, String address) {
        this.id = id;
        this.selectedMedicine = selectedMedicine;
        this.quantity = quantity;
        this.ngo = ngo;
        this.name = name;
        this.mobile = mobile;
        this.address = address;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSelectedMedicine() {
        return selectedMedicine;
    }

    public void setSelectedMedicine(String selectedMedicine) {
        this.selectedMedicine = selectedMedicine;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNgo() {
        return ngo;
    }

    public void setNgo(String ngo) {
        this.ngo = ngo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String status = "Pending";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
