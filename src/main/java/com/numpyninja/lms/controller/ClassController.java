package com.numpyninja.lms.controller;

import com.numpyninja.lms.entity.ClassEntity;
import com.numpyninja.lms.entity.ProgBatchEntity;
import com.numpyninja.lms.entity.ProgramEntity;
import com.numpyninja.lms.services.ClassServices;
import com.numpyninja.lms.services.ProgBatchServices;
import com.numpyninja.lms.services.ProgramServices;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/class")
public class ClassController extends BaseController {
    @Autowired
    private ClassServices classServices;

    @Autowired
    public ProgBatchServices progBatchServices;


    public ClassController() {
        super("class");
    }

    @Override
    protected List<?> getAll(String searchString) {
        if (StringUtils.isNotBlank(searchString)) {
            return classServices.getAllClasss(searchString);
        } else {
            return classServices.getAllClasss();
        }
    }

    @Override
    protected Map<String, Object> addUpdateFields(Map<String, Object> data){
        ProgBatchEntity programEntity = progBatchServices.findBatchById((Integer)data.get("batchId")).get();
        data.put("batchId", programEntity.getBatchName() + " - " + programEntity.getBatchDescription());
        return data;
    }

    private void populateDropdowns(Model model){
        model.addAttribute("batch", progBatchServices.getAllBatches());
    }

    @GetMapping("/addView")
    String addClass(Model model) {
        model.addAttribute("model", new ClassEntity());
        populateDropdowns(model);
        return "LmsAddclass";
    }

    //Display page to edit a Class
    @GetMapping("/editView/{id}")
    String editClass(Model model, @PathVariable Long id) {
        model.addAttribute("model", classServices.findClass(id).get());
        populateDropdowns(model);
        return "LmsEditClass";
    }

    //Create New Class
    @PostMapping("/add")
    String createClass(@ModelAttribute @Valid ClassEntity classEntity, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", formatErrors(bindingResult));
            model.addAttribute("model", classEntity);
            return "LmsAddclass";
        }
        classEntity = classServices.createClass(classEntity);
        redirectAttributes.addFlashAttribute("message", "Class " + classEntity.getClassTopic() + " with Id: " + classEntity.getCsId() + " created Successfully!");
        return "redirect:/class";
    }

    //Update class Information
    @PutMapping("/save/{id}")
    String updateClass(@ModelAttribute @Valid ClassEntity classEntity, BindingResult bindingResult, @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", formatErrors(bindingResult));
            model.addAttribute("model", classEntity);
            return "LmsEditClass";
        }
        classServices.updateClass(classEntity);
        redirectAttributes.addFlashAttribute("message", "Class " + classEntity.getClassTopic() + " with Id: " + id + " updated Successfully!");
        return "redirect:/class";
    }

    //Delete a class from page request
    @GetMapping("/delete/{id}")
    String deleteClass(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        classServices.deleteClass(id);
        redirectAttributes.addFlashAttribute("message", "Class with Id: " + id + " deleted Successfully!");
        return "redirect:/class";
    }
}
