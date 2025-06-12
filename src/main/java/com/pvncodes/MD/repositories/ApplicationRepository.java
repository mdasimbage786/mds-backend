package com.pvncodes.MD.repositories;

import com.pvncodes.MD.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    long countByStatus(String status);

}
