package edu.pdx.cs410J.yl6;

import java.util.Date;

/**
 * Appointment is the class that store appointment information for a certain
 * appointment, which includes a description, begin date and time, end date and
 * time. The begin date and time, and end date and time can be any string, it
 * leaves the client program to specify a typical format for its uses.
 */
public class Appointment extends AppointmentSlot {

  protected String description;

  public Appointment(Date begin, Date end, String description) {
    super(begin, end);
    this.description = description;
  }

  public Appointment(String appointmentId, Date begin, Date end, String description) {
    super(appointmentId, begin, end);
    this.description = description;
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

  public AppointmentSlot getAppointmentSlot() {
    return new AppointmentSlot(this.appointmentId.toString(), this.begin, this.end);
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
  public int compareTo(AppointmentSlot appt) {
    return super.compareTo(appt) != 0 ? super.compareTo(appt) : this.description.compareTo(appt.getDescription());
  }

}
