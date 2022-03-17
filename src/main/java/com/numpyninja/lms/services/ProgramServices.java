package com.numpyninja.lms.services;

import com.numpyninja.lms.entity.ProgramEntity;
import com.numpyninja.lms.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ProgramServices {
    @Autowired
    private ProgramRepository programRepository;

    public List<ProgramEntity> getAllPrograms() {
        return programRepository.findAll();
    }

    public List<ProgramEntity> getAllPrograms(String searchString) {
        return programRepository.findByProgramNameContainingIgnoreCaseOrderByProgramIdAsc(searchString);
    }

    public Optional<ProgramEntity> findProgram(Long id) {
        return programRepository.findById(id);
    }

    public ProgramEntity createProgram(ProgramEntity newProgram) {
        return programRepository.saveAndFlush(newProgram);
    }

    public ProgramEntity updateProgram(ProgramEntity updatedProgram) {
        return programRepository.save(updatedProgram);
    }

    public void deleteProgram(Long id) {
        programRepository.deleteById(id);
    }

}
