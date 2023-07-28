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
        String phoneNumberStr = contactField.toString();
        String allCountryRegex = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$";
        return phoneNumberStr != null && phoneNumberStr.equals(allCountryRegex)
             && (phoneNumberStr.length()> 8) && (phoneNumberStr.length() < 14);

//        String phoneNumberStr = contactField.toString();
//
//        // Remove all non-numeric characters from the phone number
//        String digitsOnly = phoneNumberStr.replaceAll("\\D", "");
//
//        // Check if the resulting number is within a reasonable length range
//        return digitsOnly.length() >= 10 && digitsOnly.length() <= 15;
//
//      String allCountryRegex = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$";
//            if (Pattern.compile(allCountryRegex).matcher(contactField.toString()).matches()) {
//                System.out.println("yes its a valid format");
//            } else {
//                System.out.println("Enter phone no correct format");
//                throw new InvalidDataException("Enter phone no in this format (CountryCode)(PhoneNo) +91 1234567890");
//            }
//
//        return false;
    }
//    @Override
//    public boolean isValid(String contactField,
//                           ConstraintValidatorContext cxt) {
//        return contactField != null && contactField.matches("[0-9]+")
//                && (contactField.length() > 8) && (contactField.length() < 14);
//    }

}