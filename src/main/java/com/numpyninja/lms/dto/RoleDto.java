package com.numpyninja.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleDto {

    private String roleId;
    private String roleName;
    private String roleDesc;
    private Timestamp creationTime;
    private Timestamp lastModTime;
}
