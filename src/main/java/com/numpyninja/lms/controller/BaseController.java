package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.PageStructure;
import com.numpyninja.lms.model.ListDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseController {
    public String item = "";
    @Autowired
    private PageStructure pageStructure;
    @Autowired
    private ObjectMapper objectMapper;

    private static SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy h:mm a");

    public BaseController(String item) {
        this.item = item;
    }

    private List<Map<String, Object>> transformData(List<?> data) {
        objectMapper.setDateFormat(df);
        List<Map<String, Object>> result = new ArrayList<>();
        if (data != null & !data.isEmpty()) {
            for (Object item : data) {
                result.add(addUpdateFields(objectMapper.convertValue(item, Map.class)));
            }
        }
        return result;
    }

    protected List<String> formatErrors(BindingResult bindingResult){
        return bindingResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
    }

    @GetMapping("")
    public String listData(Model model, @RequestParam(name = "searchString", required = false) String searchString) {
        ListDataModel listDataModel = new ListDataModel();
        listDataModel.setSearchString(searchString);
        listDataModel.setPageInfo(pageStructure.getPageInfo().get(item));
        listDataModel.setItem(item);
        listDataModel.setSearchString(searchString);
        listDataModel.setData(transformData(getAll(searchString)));
        model.addAttribute("model", listDataModel);
        return "LmsListData";
    }

    abstract protected List<?> getAll(String searchString);

    protected Map<String, Object> addUpdateFields(Map<String, Object> data){
        return data;
    }
}
