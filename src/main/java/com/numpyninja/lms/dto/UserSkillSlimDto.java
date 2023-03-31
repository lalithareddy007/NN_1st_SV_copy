package com.numpyninja.lms.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSkillSlimDto {

    private int skillId;

    private String skillName;

    private int months;

}
