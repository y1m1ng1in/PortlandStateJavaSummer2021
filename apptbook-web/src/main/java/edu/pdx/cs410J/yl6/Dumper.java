package edu.pdx.cs410J.yl6;

import java.io.IOException;
import java.util.Collection;

/**
 * Dumper is the interface that classes implement when functionality of dumper
 * an instance of type <code>T</code> is required. Usually the instance of
 * <code>T</code> is dumped via some {@link java.io.Writer}.
 * <p>
 * This interface also has a default static method
 * <code>dumpAppointmentBook</code> which takes a "meta information" dumper that
 * dumps any appointment book meta information (for example, the name of the
 * owner), and a "entry" dumper that dumps a single appointment. The method
 * dumps the appointment book's meta data first, then iteratively dumps each
 * appointment (the "entry") until all appointments are dumped.
 * <p>
 * <b>Note:</b> Due to this project has a lot of things to be done, currently
 * only dumper classes for "parseable" data (which means data can be written to
 * file and parsed back to object) can be passed into this default static
 * method. The classes of "prtty-printable" data can also implements interface
 * in the future, and use <code>dumpAppointmentBook</code> to avoid repeated
 * code pattern.
 */
public interface Dumper<T> {

  /**
   * Dump an object specified inside of this method
   * 
   * @param object the instance to be dumped
   * @throws IOException If an input or output exception occurs
   */
  void dump(T object) throws IOException;

  /**
   * Dump the whole {@link AppointmentBook} via a <code>metaDumper</code> which
   * dumps book's information such as the name of the owner, followed by a
   * <code>entryDumper</code> which dumps an individual appointment. This method
   * iteratively dumpers each appointments, the order is same as using an enhanced
   * <code>for</code> loop over a {@link Collection} returned by
   * {@link AppointmentBook#getAppointments()}
   * 
   * @param book        the appointment book to be dumped
   * @param metaDumper  the dumper that dumps meta information of
   *                    <code>book</code>
   * @param entryDumper the dumper that dumps individual appointment
   * @throws IOException If an input or output exception occurs
   */
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
