package com.numpyninja.lms.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
public class PageInfo {
    String title;
    String idField;
    String searchField;
    LinkedHashMap<String, String> fieldNames;
}
