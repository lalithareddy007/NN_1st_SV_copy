package com.numpyninja.lms.util;


public final class Constants {

	//Used for first names where names can contain alphabets only
	public static final String REGEX_MIN_2_ALPHABET = "^[a-zA-Z][a-zA-Z ]+$"  ;
	public static final String MSG_ALPHABET_ONLY_MIN_2 = "must contain two or more alphabets only";
	
	//Used for String like middle name initials, lastnames- which are alphabets and can be single character or more
	public static final String REGEX_MIN_1_ALPHABET = "^[a-zA-Z]+$";
	public static final String MSG_ALPHABET_ONLY_MIN_1 = "must contain one or more alphabets only";
	
	//Used for strings where String must begin with Alphabet and should have at-least 2 characters
	public static final String REGEX_MIN_2_ALPHA_NUMERIC = "^[a-zA-Z][a-zA-Z0-9 -_]+$";
	public static final String MSG_ALPHANUMERIC_ONLY_MIN_2 = "must begin with alphabet and can contain only alphanumeric characters";
	
	//Used to check if linked in url is in correct format
	public static final String REGEX_LINKEDIN_URL= ".*www.linkedin.com.*";
	public static final String MSG_INVALID_LINKEDIN_URL = "must contain www.linkedin.com";
	
	//Can be used for matching string values for Description  
	public static final String REGEX_DESC_ALPHA_NUMERIC_SPCL ="^[a-zA-Z0-9]+([a-zA-Z0-9 ]*[,_ .><?;:!#_\\-'\"%]?)+$";
	public static final String MSG_DESC_ALPHA_NUMERIC_SPCL = "must begin with alphabet and cannot contain characters like (,),@,&,$,* etc.";
}

