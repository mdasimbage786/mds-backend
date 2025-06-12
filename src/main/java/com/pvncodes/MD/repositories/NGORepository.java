package com.pvncodes.MD.repositories;

import com.pvncodes.MD.models.NGO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NGORepository  extends JpaRepository<NGO, Long>{
    Optional<NGO> findByEmail(String email);
    Optional<NGO> findByNgoId(String ngoId);
}
