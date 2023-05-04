package com.numpyninja.lms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDetails {
    private String recipient;
    private String cc;
    private String content;
    private String subject;
    //private String hyperLink;
    private Map<String, Object> model;
}
