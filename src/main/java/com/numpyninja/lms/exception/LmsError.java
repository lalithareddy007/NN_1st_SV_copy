package com.numpyninja.lms.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(content = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class LmsError {
    private String errorCode;
    private String errorMessage;

    public LmsError(String errorCode, String message) {
        this.errorCode = errorCode;
        this.errorMessage = message;
    }
}
