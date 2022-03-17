package com.numpyninja.lms.services;

import com.numpyninja.lms.entity.ClassEntity;
import com.numpyninja.lms.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ClassServices {
    @Autowired
    private ClassRepository classRepository;

    public List<ClassEntity> getAllClasss() {
        return classRepository.findAll();
    }

    public List<ClassEntity> getAllClasss(String searchString) {
        return classRepository.findByClassTopicContainingIgnoreCaseOrderByClassTopicAsc(searchString);
    }

    public Optional<ClassEntity> findClass(Long id) {
        return classRepository.findById(id);
    }

    public ClassEntity createClass(ClassEntity newClass) {
        return classRepository.saveAndFlush(newClass);
    }

    public ClassEntity updateClass(ClassEntity updatedClass) {
        return classRepository.save(updatedClass);
    }

    public void deleteClass(Long id) {
        classRepository.deleteById(id);
    }

}
