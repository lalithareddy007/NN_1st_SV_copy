package com.numpyninja.lms.controller;

import com.numpyninja.lms.entity.AttendanceEntity;
import com.numpyninja.lms.services.AttendanceServices;
import com.numpyninja.lms.services.ClassServices;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/attendance")
@Slf4j
public class AttendanceController extends BaseController {
    @Autowired
    private AttendanceServices attendanceServices;

    @Autowired
    private ClassServices classServices;

    public AttendanceController() {
        super("attendance");
    }

    @Override
    protected List<?> getAll(String searchString) {
        log.info("Inside attendance get all");
        if (StringUtils.isNotBlank(searchString)) {
            return attendanceServices.getAllAttendances(searchString);
        } else {
            return attendanceServices.getAllAttendances();
        }
    }

    private void populateDropdowns(Model model) {
        model.addAttribute("class", classServices.getAllClasss());
    }

    @GetMapping("/addView")
    String addAttendance(Model model) {
        model.addAttribute("model", new AttendanceEntity());
        populateDropdowns(model);
        return "LmsAddattendance";
    }

    //Display page to edit a Attendance
    @GetMapping("/editView/{id}")
    String editAttendance(Model model, @PathVariable Long id) {
        model.addAttribute("model", attendanceServices.findAttendance(id).get());
        populateDropdowns(model);
        return "LmsEditAttendance";
    }

    //Create New Attendance
    @PostMapping("/add")
    String createAttendance(@ModelAttribute @Valid AttendanceEntity attendanceEntity, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", formatErrors(bindingResult));
            model.addAttribute("model", attendanceEntity);
            return "LmsAddattendance";
        }
        attendanceEntity = attendanceServices.createAttendance(attendanceEntity);
        redirectAttributes.addFlashAttribute("message", "Attendance with Id: " + attendanceEntity.getAttId() + " created Successfully!");
        return "redirect:/attendance";
    }

    //Update attendance Information
    @PutMapping("/save/{id}")
    String updateAttendance(@ModelAttribute @Valid AttendanceEntity attendanceEntity, BindingResult bindingResult, @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", formatErrors(bindingResult));
            model.addAttribute("model", attendanceEntity);
            return "LmsEditAttendance";
        }
        attendanceServices.updateAttendance(attendanceEntity);
        redirectAttributes.addFlashAttribute("message", "Attendance with Id: " + id + " updated Successfully!");
        return "redirect:/attendance";
    }

    //Delete a attendance from page request
    @GetMapping("/delete/{id}")
    String deleteAttendance(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        attendanceServices.deleteAttendance(id);
        redirectAttributes.addFlashAttribute("message", "Attendance with Id: " + id + " deleted Successfully!");
        return "redirect:/attendance";
    }
}
