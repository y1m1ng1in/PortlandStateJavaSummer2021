package edu.pdx.cs410J.yl6;

import java.util.Arrays;
import java.util.HashSet;

public class EnumStringValidator extends AbstractValidator {
  
  private HashSet<String> options;
  private String message;

  public EnumStringValidator() {
    this.options = new HashSet<String>();
  }

  @Override
  public boolean isValid(String s) {
    if (this.options.contains(s)) {
      return true;
    }
    this.message = 
        s + " must be one of the following: " + this.options.toString();
    return false;
  }

  /**
   * Return error message generated by last <code>isValid</code> call.
   * 
   * @return error message computed during last <code>isValid</code> call.
   */
  @Override
  public String getErrorMessage() {
    return this.message;
  }

  public EnumStringValidator addOption(String s) {
    this.options.add(s);
    return this;
  }
}