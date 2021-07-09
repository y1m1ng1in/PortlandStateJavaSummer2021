package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.AbstractAppointment;

/**
 * Appointment is the class that store appointment information for a certain
 * appointment, which includes a description, begin date and time, end date 
 * and time. The begin date and time, and end date and time can be any string, 
 * it leaves the client program to specify a typical format for its uses.
 */
public class Appointment extends AbstractAppointment implements PlainTextRepresentable {
  
  private String beginTime;
  private String endTime;
  private String beginDate;
  private String endDate;
  private String description;
  static final int numberOfField = 5;

  /**
   * Create a appointment. 
   * 
   * @param beginDate   a string of the begin date of the appointment
   * @param beginTime   a string of the begin time of the appointment
   * @param endDate     a string of the end date of the appointment
   * @param endTime     a string of the end time of the appointment
   * @param description a description of the appointment
   */
  public Appointment(String beginDate, String beginTime, 
                     String endDate, String endTime, String description) {
    this.beginDate = beginDate;
    this.beginTime = beginTime;
    this.endDate = endDate;
    this.endTime = endTime;
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
    return this.beginDate + " " + this.beginTime;
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
    return this.endDate + " " + this.endTime;
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

  @Override
  public String[] getStringFields() {
    String[] fields = new String[numberOfField];
    fields[0] = this.beginDate;
    fields[1] = this.beginTime;
    fields[2] = this.endDate;
    fields[3] = this.endTime;
    fields[4] = this.description;
    return fields;
  }

  @Override
  public int getExpectedNumberOfField() {
    return numberOfField;
  }

}
