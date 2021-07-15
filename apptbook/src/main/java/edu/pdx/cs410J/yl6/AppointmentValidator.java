package edu.pdx.cs410J.yl6;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * AppointmentValidator is the class that validates an array of <code>String</code>s 
 * that stores arguments to be passed into {@link Appointment} constructor. 
 * <p>
 * This class is designed to be used for validating an array of strings that either
 * comes from commandline arguments, or are parsed from external sources, which are 
 * to be used for constructing <code>Appointment</code> instance. If any validation
 * violation happens, then corresponding error message will be stored in 
 * <code>errorMessage</code> which can be accessed by <code>getErrorMessage</code>.
 * <code>errorMessage</code> is generated when public method <code>isValid</code> is
 * invoked, thus <code>errorMessage</code> always represents error comes from the 
 * last <code>isValid</code> call.
 */
public class AppointmentValidator {

  private String errorMessage;
  private String dateStringPattern;

  /**
   * Create an AppointmentValidator instance
   * 
   * @param dataStringPattern 
   *        the pattern that is used for {@link SimpleDateFormat} construction, which
   *        is used to parse a string represents date and time. If any exception 
   *        raise during <code>parse</code> of <code>SimpleDateFormat</code> instance,
   *        then it indicates that string is not in expected format.
   */
  public AppointmentValidator(String dateStringPattern) {
    this.dateStringPattern = dateStringPattern;
  }

  /**
   * Check if an array <code>fields</code> of strings are valid arguments to 
   * construct an {@link Appointment} instance.
   * 
   * @param fields an array of strings to be passed into <code>Appointment</code>
   *               constructor.
   * @return       <code>true</code> if all strings of <code>fields</code> are 
   *               valid; <code>false</code> otherwise.
   */
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

  /**
   * Get error message reported by the last call of <code>isValid</code>
   * 
   * @return error message reported by the last call of <code>isValid</code>
   */
  public String getErrorMessage() {
    return this.errorMessage;
  }
}