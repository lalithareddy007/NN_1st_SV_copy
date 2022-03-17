package com.numpyninja.lms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemData {
    String id = "";
    String description = "";
    @Override
    public String toString() {
        return id + " " + description;
    }
}
