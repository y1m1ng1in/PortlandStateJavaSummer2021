package edu.pdx.cs410J.yl6;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * AppointmentValidator is the class that validates an array of
 * <code>String</code>s that stores arguments to be passed into
 * {@link Appointment} constructor.
 * <p>
 * This class is designed to be used for validating an array of strings that
 * either comes from commandline arguments, or are parsed from external sources,
 * which are to be used for constructing <code>Appointment</code> instance. If
 * any validation violation happens, then corresponding error message will be
 * stored in <code>errorMessage</code> which can be accessed by
 * <code>getErrorMessage</code>. <code>errorMessage</code> is generated when
 * public method <code>isValid</code> is invoked, thus <code>errorMessage</code>
 * always represents error comes from the last <code>isValid</code> call.
 */
public class AppointmentValidator {

  private String errorMessage;
  private String dateStringPattern;
  private Date begin;
  private Date end;
  private String description;

  /**
   * Create an AppointmentValidator instance
   * 
   * @param dateStringPattern the pattern that is used for
   *                          {@link SimpleDateFormat} construction, which is used
   *                          to parse a string represents date and time. If any
   *                          exception raise during <code>parse</code> of
   *                          <code>SimpleDateFormat</code> instance, then it
   *                          indicates that string is not in expected format.
   */
  public AppointmentValidator(String dateStringPattern) {
    this.dateStringPattern = dateStringPattern;
  }

  /**
   * Create an AppointmentValidator instance using {@link Appointment} default
   * date string pattern
   */
  public AppointmentValidator() {
    this.dateStringPattern = Helper.datePattern;
  }

  /**
   * Create a {@link Appointment} instance after validating all
   * <code>String</code> fields that are to construct related fields. This method
   * is a wrapper of {@link AppointmentValidator#isValid} such that if it
   * indicates that every string is valid, then create an <code>Appointment</code>
   * instance; otherwise return null, and all the error message can be accessed
   * via {@link AppointmentValidator#getErrorMessage}
   * 
   * @param begin       the string represents begin time
   * @param end         the string represents end time
   * @param description the description of appointment
   * @return an <code>Appointment</code> instance if arguments passed into this
   *         method are valid; <code>null</code> otherwise
   */
  public Appointment createAppointmentFromString(String begin, String end, String description) {
    if (!isValid(new String[] { begin, end, description })) {
      return null;
    }
    return new Appointment(this.begin, this.end, this.description);
  }

  public Appointment createAppointmentFromString(String id, String begin, String end, String description) {
    if (!isValid(new String[] { begin, end, description })) {
      return null;
    }
    return new Appointment(id, this.begin, this.end, this.description);
  }

  /**
   * Check if an array <code>fields</code> of strings are valid arguments to
   * construct an {@link Appointment} instance.
   * 
   * @param fields an array of strings to be passed into <code>Appointment</code>
   *               constructor.
   * @return <code>true</code> if all strings of <code>fields</code> are valid;
   *         <code>false</code> otherwise.
   */
  public boolean isValid(String[] fields) {
    NonemptyStringValidator nonemptyField = new NonemptyStringValidator("description");

    if (!nonemptyField.isValid(fields[2])) {
      this.errorMessage = nonemptyField.getErrorMessage();
      return false;
    }

    this.description = fields[2];

    DateFormat df = new SimpleDateFormat(this.dateStringPattern);
    df.setLenient(false);
    try {
      Date begin = df.parse(fields[0]);
      Date end = df.parse(fields[1]);
      if (!begin.before(end)) {
        this.errorMessage = "Begin time is not early than end time of appointment, begin at " + fields[0]
            + ", but end at " + fields[1];
        return false;
      }
      this.begin = begin;
      this.end = end;
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