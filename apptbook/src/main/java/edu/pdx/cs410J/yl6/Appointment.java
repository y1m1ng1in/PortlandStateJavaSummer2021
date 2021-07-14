package edu.pdx.cs410J.yl6;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

import edu.pdx.cs410J.AbstractAppointment;

/**
 * Appointment is the class that store appointment information for a certain
 * appointment, which includes a description, begin date and time, end date 
 * and time. The begin date and time, and end date and time can be any string, 
 * it leaves the client program to specify a typical format for its uses.
 */
public class Appointment extends AbstractAppointment 
    implements PlainTextRepresentable, PrettyPrintable, Comparable<Appointment> {
  
  private String beginString;
  private String endString;
  private String description;
  private Date begin;
  private Date end;
  static final int numberOfField = 3;

  /**
   * 
   * @param begin
   * @param end
   * @param description
   * @throws AppointmentInvalidFieldException
   */
  public Appointment(String begin, String end, String description) 
      throws AppointmentInvalidFieldException {
    DateFormat df = new SimpleDateFormat("M/d/yyyy h:m a");
    df.setLenient(false);
    try {
      this.begin = df.parse(begin);
      this.end = df.parse(end);
    } catch (ParseException ex) {
      throw new AppointmentInvalidFieldException(ex.getMessage());
    }

    this.beginString = begin;
    this.endString = end;
    this.description = description;
  }

  /**                                                                                 
   * Returns a String describing the beginning date and time of this
   * appointment.        
   * 
   * @return a string describing the beginning date and time of this
   *         appointment.                                
   */ 
  @Override
  public String getBeginTimeString() {
    return DateFormat.getInstance().format(this.begin);
  }

  /**
   * Returns a String describing the ending date and time of this
   * appointment.
   * 
   * @return a string describing the ending date and time of this
   *         appointment.
   */
  @Override
  public String getEndTimeString() {
    return DateFormat.getInstance().format(this.end);
  }

  /**
   * Returns a description of this appointment (for instance,
   * <code>"Have coffee with Marsha"</code>).
   * 
   * @return a string of the description of the appointment
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * 
   */
  @Override
  public String[] getStringFields() {
    String[] fields = new String[numberOfField];
    fields[0] = this.beginString;
    fields[1] = this.endString;
    fields[2] = this.description;
    return fields;
  }

  /**
   * 
   */
  @Override
  public int getExpectedNumberOfField() {
    return numberOfField;
  }

  /**
   * Returns the {@link Date} that this appointment begins.
   */
  public Date getBeginTime() {
    return this.begin;
  }

  /**
   * Returns the {@link Date} that this appointment ends.
   */
  public Date getEndTime() {
    return this.end;
  }

  /**
   * 
   */
  @Override
  public int compareTo(Appointment appt) {
    if (this.begin.equals(appt.begin) && this.end.equals(appt.end)) {
      return this.description.compareTo(appt.description);
    }
    if (this.begin.equals(appt.begin)) {
      return this.end.compareTo(appt.end);
    }
    return this.begin.compareTo(appt.begin);
  }

  @Override
  public String[] getPrettyPrinterFields() {
    String[] fields = new String[numberOfField + 1];
    fields[0] = getBeginTimeString();
    fields[1] = getEndTimeString();
    fields[2] = this.description;
    fields[3] = String.valueOf(((int) (this.end.getTime() - this.begin.getTime()) / 60000));
    return fields;
  }

}
