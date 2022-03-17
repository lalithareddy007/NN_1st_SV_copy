package com.numpyninja.lms.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SelectDataModel {
    String item;
    PageInfo pageInfo;
    List<ItemData> data = new ArrayList<>();
    String searchString;
    String selected = "";
}
