package com.numpyninja.lms.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ListDataModel {
    String item;
    PageInfo pageInfo;
    List<Map<String, Object>> data;
    String searchString;
}
