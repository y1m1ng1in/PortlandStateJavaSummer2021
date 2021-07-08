package edu.pdx.cs410J.yl6;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateStringValidator extends AbstractValidator {
  static final int MAX_MONTH = 12;
  static final int MAX_DAY = 31;
  private final String pattern;
  private String message;
  
  public DateStringValidator(String pattern) {
    this.pattern = pattern;
  }

  @Override
  public boolean isValid(String s) {
    Pattern r = Pattern.compile(this.pattern);
    Matcher m = r.matcher(s);
    
    if (!m.matches()) {
      this.message = "date " + s + " format does not meet requirement";
      return false;
    }
    
    int month = Integer.parseInt(m.group(1));
    int day = Integer.parseInt(m.group(2));
    if (month > MAX_MONTH) {
      this.message = month + " is not a valid month";
      return false;
    }
    if (day > MAX_DAY) {
      this.message = day + " is not a valid day";
      return false;
    }

    return true;
  }

  @Override
  public String getErrorMessage() {
    return this.message;
  }
}