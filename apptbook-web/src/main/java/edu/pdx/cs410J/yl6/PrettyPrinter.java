package edu.pdx.cs410J.yl6;

import java.io.IOException;
import java.io.Writer;

import edu.pdx.cs410J.AppointmentBookDumper;

/**
 * PrettyPrinter is the class that creates a nicely-formatted textual
 * presentation of an appointment book.
 */
public class PrettyPrinter implements AppointmentBookDumper<AppointmentBook<Appointment>> {

  private Writer writer;

  /**
   * Create a TextDumper instance with specified relative path of the file to be
   * written.
   * 
   * @param writer the {@link Writer} instance that is to be used to write
   */
  public PrettyPrinter(Writer writer) {
    this.writer = writer;
  }

  public void dump(AppointmentBook<Appointment> book) throws IOException {
    PrettyPrintableAppointmentBookDumper bookDumper = new PrettyPrintableAppointmentBookDumper(writer);
    PrettyPrintableAppointmentDumper appointmentDumper = new PrettyPrintableAppointmentDumper(writer);
    Dumper.dumpAppointmentBook(book, bookDumper, appointmentDumper);
  }

}