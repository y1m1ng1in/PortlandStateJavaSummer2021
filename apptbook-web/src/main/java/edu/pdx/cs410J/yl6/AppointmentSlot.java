package edu.pdx.cs410J.yl6;

import java.util.Date;
import java.util.UUID;

import edu.pdx.cs410J.AbstractAppointment;

public class AppointmentSlot extends AbstractAppointment implements Comparable<AppointmentSlot> {

  protected Date begin;
  protected Date end;
  final protected UUID appointmentId;

  public AppointmentSlot(Date begin, Date end) {
    this.begin = begin;
    this.end = end;
    this.appointmentId = UUID.randomUUID();
  }

  public AppointmentSlot(String id, Date begin, Date end) {
    this.begin = begin;
    this.end = end;
    this.appointmentId = UUID.fromString(id);
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
   * Returns a String describing the beginning date and time of this appointment.
   * 
   * @return a string describing the beginning date and time of this appointment.
   */
  @Override
  public String getBeginTimeString() {
    return Helper.getDateString(this.begin);
  }

  /**
   * Returns a String describing the ending date and time of this appointment.
   * 
   * @return a string describing the ending date and time of this appointment.
   */
  @Override
  public String getEndTimeString() {
    return Helper.getDateString(this.end);
  }

  /**
   * Returns a description of this appointment (for instance,
   * <code>"Have coffee with Marsha"</code>).
   * 
   * @return a string of the description of the appointment
   */
  @Override
  public String getDescription() {
    return "";
  }

  /**
   * Returns a string for appointment Id.
   * 
   * @return a string for appointment Id
   */
  public String getId() {
    return this.appointmentId.toString();
  }

  @Override
  public int compareTo(AppointmentSlot other) {
    if (this.begin.equals(other.begin)) {
      return this.end.compareTo(other.end);
    }
    return this.begin.compareTo(other.begin);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof AppointmentSlot)) {
      return false;
    }
    AppointmentSlot other = (AppointmentSlot) o;
    // reference to {@link AppointmentSlot#compareTo} two slots are same if and only
    // if lower bounds and upper bounds are both same
    return this.end.equals(other.end) && this.begin.equals(other.begin);
  }

}
