package com.numpyninja.lms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.numpyninja.lms.config.UserIDGenerator;
import org.hibernate.annotations.Parameter;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@EqualsAndHashCode

@Table(name="tbl_lms_userskill_map")
public class UserSkill {
  @Id
  @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "user_skill_id_generator")
  @GenericGenerator(name="user_skill_id_generator",strategy = "com.numpyninja.lms.config.UserIDGenerator",
  parameters = {
          @Parameter(name = UserIDGenerator.INCREMENT_PARAM, value = "1"),
          @Parameter(name = UserIDGenerator.VALUE_PREFIX_PARAMETER, value = "US"),
          @Parameter(name = UserIDGenerator.NUMBER_FORMAT_PARAMETER, value = "%02d")})
  @Column(name = "user_skill_id")
  private String userSkillId;

    @ManyToOne (   fetch = FetchType.LAZY )
    @JoinColumn ( name = "user_id", nullable = false )
    private User user;

    @ManyToOne (fetch=FetchType.LAZY)
    @JoinColumn(name ="skill_id",nullable = false)
    private  SkillMaster skill;


    @Column(name="months_of_exp")
    private int months;

    @JsonIgnore
    @Column( name ="creation_time")
    private Timestamp creationTime;

    @JsonIgnore
    @Column( name ="last_mod_time")
    private Timestamp lastModTime;

}
