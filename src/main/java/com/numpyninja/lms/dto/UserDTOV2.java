package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTOV2 {

    @JsonProperty("users")
    List<UserDto> users;
    @JsonProperty("facets")
    FacetDto facets;

}
