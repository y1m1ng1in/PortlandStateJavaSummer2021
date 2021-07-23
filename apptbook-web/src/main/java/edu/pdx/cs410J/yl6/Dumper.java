package edu.pdx.cs410J.yl6;

import java.io.IOException;
import java.util.Collection;

public interface Dumper<T> {

  public abstract void dump(T object) throws IOException;

  static public void dumpAppointmentBook(AppointmentBook<Appointment> book,
      Dumper<AppointmentBook<Appointment>> metaDumper, Dumper<Appointment> entryDumper) throws IOException {
    // Get all appointments in the book
    Collection<Appointment> appts = book.getAppointments();
    
    // Dump meta information first
    metaDumper.dump(book);

    // followed by each appointments
    for (Appointment appt : appts) {
      entryDumper.dump(appt);
    }
  }

}
