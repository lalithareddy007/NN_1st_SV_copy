package com.numpyninja.lms.config;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements
        ConstraintValidator<PhoneNumberConstraint, Long> {

    @Override
    public void initialize(PhoneNumberConstraint phoneNumber) {
    }

    @Override
    public boolean isValid(Long contactField, ConstraintValidatorContext context) {
        return contactField != null && contactField.equals("[0-9]+")
                && (contactField.longValue() > 8) && (contactField.longValue() < 14);
    }

//    @Override
//    public boolean isValid(String contactField,
//                           ConstraintValidatorContext cxt) {
//        return contactField != null && contactField.matches("[0-9]+")
//                && (contactField.length() > 8) && (contactField.length() < 14);
//    }

}