package edu.pdx.cs410J.yl6;

import java.util.Date;
import java.util.UUID;
import java.text.SimpleDateFormat;

import edu.pdx.cs410J.AbstractAppointment;

/**
 * Appointment is the class that store appointment information for a certain
 * appointment, which includes a description, begin date and time, end date and
 * time. The begin date and time, and end date and time can be any string, it
 * leaves the client program to specify a typical format for its uses.
 */
public class Appointment extends AbstractAppointment implements Comparable<Appointment> {

  static public int nextid = 0;
  final static public String dateFormat = "M/d/yyyy h:m a";
  final static public SimpleDateFormat outputDateFormat = new SimpleDateFormat("M/d/yyyy h:m a");

  protected String description;
  protected Date begin;
  protected Date end;

  final protected int id;
  final protected UUID ownerId;

  public Appointment(Date begin, Date end, String description) {
    this.begin = begin;
    this.end = end;
    this.description = description;
    this.ownerId = null;
    this.id = ++nextid;
  }

  public Appointment(UUID ownerId, Date begin, Date end, String description) {
    this.ownerId = ownerId;
    this.begin = begin;
    this.end = end;
    this.description = description;
    this.id = ++nextid;
  }

  /**
   * Returns a String describing the beginning date and time of this appointment.
   * 
   * @return a string describing the beginning date and time of this appointment.
   */
  @Override
  public String getBeginTimeString() {
    return outputDateFormat.format(this.begin);
  }

  /**
   * Returns a String describing the ending date and time of this appointment.
   * 
   * @return a string describing the ending date and time of this appointment.
   */
  @Override
  public String getEndTimeString() {
    return outputDateFormat.format(this.end);
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

  public UUID getOwnerId() {
    return this.ownerId;
  }

  public String getOwnerIdString() {
    return this.ownerId.toString();
  }

  /**
   * Compares invoking appointment with appointment <code>appt</code> passed in.
   * If both begin time and end time are same between two appointments, then two
   * appointments are ordered by description lexicographically. Otherwise, if
   * begin time between two are same, then ordered by end time; otherwise, ordered
   * by their begin time.
   * 
   * @param appt the <code>Appointment</code> instance that is to be compared with
   *             invoking appointment.
   * @return 1 if invoking appointment is ordered after parameter; 0 if two
   *         appointments are same (which means both begin, end time, and
   *         description are same) -1 if invoking appointment is ordered before
   *         parameter.
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

}
