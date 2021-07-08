package edu.pdx.cs410J.yl6;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeStringValidator extends AbstractValidator {
  static final int MAX_HOUR = 23;
  static final int MAX_MINUTE = 59;
  private final String pattern;
  private String message;
  
  public TimeStringValidator(String pattern) {
    this.pattern = pattern;
  }

  @Override
  public boolean isValid(String s) {
    Pattern r = Pattern.compile(this.pattern);
    Matcher m = r.matcher(s);
    
    if (!m.matches()) {
      this.message = "time " + s + " format does not meet requirement";
      return false;
    }

    int hour = Integer.parseInt(m.group(1));
    int minute = Integer.parseInt(m.group(2));
    if (hour > MAX_HOUR) {
      this.message = hour + " is not a valid hour";
      return false;
    }
    if (minute > MAX_MINUTE) {
      this.message = minute + " is not a valid minute";
      return false;
    }

    return true;
  }

  @Override
  public String getErrorMessage() {
    return this.message;
  }
}