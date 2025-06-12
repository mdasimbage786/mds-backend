package com.pvncodes.MD.services;


import com.pvncodes.MD.models.NGO;
import com.pvncodes.MD.repositories.NGORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NGOService {
    @Autowired
    private NGORepository ngoRepository;

    public NGO saveNGO(NGO ngo) {
        return ngoRepository.save(ngo);
    }

    public Optional<NGO> findByEmail(String email) {
        return ngoRepository.findByEmail(email);
    }

    public Optional<NGO> findByNgoId(String ngoId) {
        return ngoRepository.findByNgoId(ngoId);
    }

    public List<NGO> findAllNGOs() {
        return ngoRepository.findAll();
    }

    public Optional<NGO> findById(Long id) {
        return ngoRepository.findById(id);
    }

    public void deleteNGO(Long id) {
        ngoRepository.deleteById(id);
    }
}
