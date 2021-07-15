package edu.pdx.cs410J.yl6;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

public class AppointmentValidator {

  private String errorMessage;
  private String dateStringPattern;

  public AppointmentValidator(String dateStringPattern) {
    this.dateStringPattern = dateStringPattern;
  }

  public boolean isValid(String[] fields) {
    NonemptyStringValidator nonemptyField = new NonemptyStringValidator("description");

    if (!nonemptyField.isValid(fields[2])) {
      this.errorMessage = nonemptyField.getErrorMessage();
      return false;
    }

    DateFormat df = new SimpleDateFormat(this.dateStringPattern);
    df.setLenient(false);
    try {
      Date begin = df.parse(fields[0]);
      Date end = df.parse(fields[1]);
      if (!begin.before(end)) {
        this.errorMessage = 
            "Begin time is late than end time of appointment, begin at " + 
            fields[0] + ", but end at " + fields[1];
        return false;   
      }   
    } catch (ParseException ex) {
      this.errorMessage = ex.getMessage();
      return false;
    }

    return true;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }
}