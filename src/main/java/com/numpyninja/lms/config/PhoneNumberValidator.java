package com.numpyninja.lms.config;

import com.numpyninja.lms.exception.InvalidDataException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements
        ConstraintValidator<PhoneNumberConstraint, Long> {

    @Override
    public void initialize(PhoneNumberConstraint phoneNumber) {
    }

    @Override
    public boolean isValid(Long contactField, ConstraintValidatorContext context) {
//        return contactField != null && contactField.equals("[0-9]+")
//                && (contactField.longValue()> 8) && (contactField.longValue() < 14);

        String allCountryRegex = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$";
            if (Pattern.compile(allCountryRegex).matcher(contactField.toString()).matches()) {
                System.out.println("yes its a valid format");
            } else {
                System.out.println("Enter phone no correct format");
                throw new InvalidDataException("Enter phone no in this format (CountryCode)(PhoneNo) +91 1234567890");
            }

        return false;
    }
//    @Override
//    public boolean isValid(String contactField,
//                           ConstraintValidatorContext cxt) {
//        return contactField != null && contactField.matches("[0-9]+")
//                && (contactField.length() > 8) && (contactField.length() < 14);
//    }

}