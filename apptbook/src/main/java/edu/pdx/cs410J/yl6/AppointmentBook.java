package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.AbstractAppointmentBook;
import edu.pdx.cs410J.AbstractAppointment;
import java.util.ArrayList;

public class AppointmentBook<T extends AbstractAppointment> extends AbstractAppointmentBook<T> {
  private ArrayList<T> appts = new ArrayList<T>();
  private String owner;
  
  public AppointmentBook(String owner) {
    this.owner = owner;
  }
  
  /**
   * Adds an appointment to this appointment book
   */
  @Override
  public void addAppointment(T appt) {
    this.appts.add(appt);
  }

  /**
   * Returns all of the appointments in this appointment book as a
   * collection of {@link AbstractAppointment}s.
   */
  @Override
  public ArrayList<T> getAppointments() {
    return this.appts;
  }

  /**
  * Returns the name of the owner of this appointment book.
  */
  @Override
  public String getOwnerName() {
    return this.owner;
  }
}
