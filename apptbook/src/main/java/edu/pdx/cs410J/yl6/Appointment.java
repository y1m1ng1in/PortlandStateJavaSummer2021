package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.AbstractAppointment;

public class Appointment extends AbstractAppointment {
  private String beginTime;
  private String endTime;
  private String beginDate;
  private String endDate;
  private String description;

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
   */ 
  @Override
  public String getBeginTimeString() {
    return this.beginDate + " " + this.beginTime;
  }

  /**
   * Returns a String describing the ending date and time of this
   * appointment.
   */
  @Override
  public String getEndTimeString() {
    return this.endDate + " " + this.endTime;
  }

  /**
   * Returns a description of this appointment (for instance,
   * <code>"Have coffee with Marsha"</code>).
   */
  @Override
  public String getDescription() {
    return this.description;
  }

}
