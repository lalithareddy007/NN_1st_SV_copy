package com.numpyninja.lms.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSkillDTO {
    private String userSkillId;
    private String userId;
    private int skillId;
    private String skillName;
    private int months;


}
