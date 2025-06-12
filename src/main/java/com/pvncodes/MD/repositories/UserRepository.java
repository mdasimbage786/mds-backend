package com.pvncodes.MD.repositories;

import com.pvncodes.MD.models.NGO;
import com.pvncodes.MD.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    interface NGORepository extends JpaRepository<NGO, Long> {
        Optional<NGO> findByEmail(String email);
        Optional<NGO> findByNgoId(String ngoId);
    }
}
