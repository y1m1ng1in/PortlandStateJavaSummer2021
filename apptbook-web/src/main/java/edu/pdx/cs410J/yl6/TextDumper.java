package edu.pdx.cs410J.yl6;

import java.io.Writer;
import java.io.IOException;

import edu.pdx.cs410J.AppointmentBookDumper;

public class TextDumper implements AppointmentBookDumper<AppointmentBook<Appointment>> {

  private Writer writer;

  /**
   * Create a TextDumper instance with specified relative path of the file to be written.
   * 
   * @param writer the {@link Writer} instance that is to be used to write
   */
  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  public void dump(AppointmentBook<Appointment> book) throws IOException {
    ParseableAppointmentBookDumper bookDumper = new ParseableAppointmentBookDumper(writer);
    ParseableAppointmentDumper appointmentDumper = new ParseableAppointmentDumper(writer);
    Dumper.dumpAppointmentBook(book, bookDumper, appointmentDumper);
  }

}