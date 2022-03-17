package com.numpyninja.lms.controller;

import com.numpyninja.lms.entity.ProgramEntity;
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

@Controller
@RequestMapping("/program")
public class ProgramController extends BaseController {
    @Autowired
    private ProgramServices programServices;

    public ProgramController() {
        super("program");
    }

    @Override
    protected List<?> getAll(String searchString) {
        if (StringUtils.isNotBlank(searchString)) {
            return programServices.getAllPrograms(searchString);
        } else {
            return programServices.getAllPrograms();
        }
    }

    @GetMapping("/addView")
    String addProgram(Model model) {
        model.addAttribute("model", new ProgramEntity());
        return "LmsAddprogram";
    }

    //Display page to edit a Program
    @GetMapping("/editView/{id}")
    String editProgram(Model model, @PathVariable Long id) {
        model.addAttribute("model", programServices.findProgram(id).get());
        return "LmsEditProgram";
    }

    //Create New Program
    @PostMapping("/add")
    String createProgram(@ModelAttribute @Valid ProgramEntity program, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", formatErrors(bindingResult));
            model.addAttribute("model", program);
            return "LmsAddprogram";
        }
        program = programServices.createProgram(program);
        redirectAttributes.addFlashAttribute("message", "Program " + program.getProgramName() + " with Id: " + program.getProgramId() + " created Successfully!");
        return "redirect:/program";
    }

    //Update program Information
    @PutMapping("/save/{id}")
    String updateProgram(@ModelAttribute @Valid ProgramEntity program, BindingResult bindingResult, @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", formatErrors(bindingResult));
            model.addAttribute("model", program);
            return "LmsEditProgram";
        }
        programServices.updateProgram(program);
        redirectAttributes.addFlashAttribute("message", "Program " + program.getProgramName() + " with Id: " + id + " updated Successfully!");
        return "redirect:/program";
    }

    //Delete a program from page request
    @GetMapping("/delete/{id}")
    String deleteProgram(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        programServices.deleteProgram(id);
        redirectAttributes.addFlashAttribute("message", "Program with Id: " + id + " deleted Successfully!");
        return "redirect:/program";
    }
}
