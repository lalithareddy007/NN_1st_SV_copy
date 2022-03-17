package com.numpyninja.lms.controller;

import com.numpyninja.lms.entity.ProgBatchEntity;
import com.numpyninja.lms.entity.ProgramEntity;
import com.numpyninja.lms.services.ProgBatchServices;
import com.numpyninja.lms.services.ProgramServices;
import io.swagger.models.auth.In;
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
@RequestMapping("/programbatch")
public class ProgBatchController extends BaseController {

    @Autowired
    private ProgBatchServices batchService;

    @Autowired
    private ProgramServices programServices;

    //Get all ProgramList
    public ProgBatchController() {
        super("programbatch");
    }

    @Override
    protected List<?> getAll(String searchString) {
        if (StringUtils.isNotBlank(searchString)) {
            return batchService.getAllBatches(searchString);
        } else {
            return batchService.getAllBatches();
        }
    }

    @Override
    protected Map<String, Object> addUpdateFields(Map<String, Object> data){
        ProgramEntity programEntity = programServices.findProgram((Long)data.get("batchProgramId")).get();
        data.put("batchProgramId", programEntity.getProgramId() + " - " + programEntity.getProgramName());
        return data;
    }

    private void populateDropdowns(Model model){
        model.addAttribute("program", programServices.getAllPrograms());
    }

    @GetMapping("/addView")
    String addProgram(Model model) {
        model.addAttribute("model", new ProgBatchEntity());
        populateDropdowns(model);
        return "LMSAddBatch";
    }

    //Display page to edit a Batch
    @GetMapping("/editView/{id}")
    String editProgram(Model model, @PathVariable Integer id) {
        model.addAttribute("model", batchService.findBatchById(id).get());
        populateDropdowns(model);
        return "LmsEditBatch";
    }

    @PostMapping("/add")
    String createProgram(@ModelAttribute @Valid ProgBatchEntity batch, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", formatErrors(bindingResult));
            model.addAttribute("model", batch);
            return "LmsAddBatch";
        }
        batch = batchService.createBatch(batch);
        redirectAttributes.addFlashAttribute("message", "Batch " + batch.getBatchName() + " with Id: " + batch.getBatchId() + " created Successfully!");
        return "redirect:/programbatch";
    }

    //Update program Information
    @PutMapping("/save/{id}")
    String updateProgram(@ModelAttribute @Valid ProgBatchEntity batch, BindingResult bindingResult, @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", formatErrors(bindingResult));
            model.addAttribute("model", batch);
            return "LMSEditBatch";
        }
        //model.addAttribute("model",batchService.updateBatch(batch,id));
        batchService.updateBatch(batch, id);
        redirectAttributes.addFlashAttribute("message", "Program " + batch.getBatchName() + " with Id: " + id + " updated Successfully!");
        return "redirect:/programbatch";
    }

    @GetMapping("/delete/{id}")
    String deleteProgram(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        batchService.deleteProgramBatch(id);
        redirectAttributes.addFlashAttribute("message", "Program Batch with Id: " + id + " deleted Successfully!");
        return "redirect:/programbatch";
    }
}
