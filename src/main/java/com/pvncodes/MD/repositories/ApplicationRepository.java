package com.pvncodes.MD.repositories;

import com.pvncodes.MD.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    long countByStatus(String status);
    List<Application> findByStatus(String status);
}