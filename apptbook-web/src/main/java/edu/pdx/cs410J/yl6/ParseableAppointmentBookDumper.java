package edu.pdx.cs410J.yl6;

import java.io.Writer;
import java.io.IOException;

/**
 * ParseableAppointmentBookDumper is the class that dumps the meta information
 * of an {@link AppointmentBook}, which is simply the name of the owner of the
 * book via a {@link Writer} specified by constructor.
 */
public class ParseableAppointmentBookDumper extends ParseableDumper<AppointmentBook<Appointment>> {

  protected Writer writer;

  /**
   * Create a ParseableAppointmentBookDumper instance
   * 
   * @param writer the {@link Writer} instance to be used in
   *               {@link ParseableAppointmentBookDumper#dump}
   */
  public ParseableAppointmentBookDumper(Writer writer) {
    this.writer = writer;
  }

  /**
   * Dump owner name of <code>book</code>
   * 
   * @param book the {@link AppointmentBook} instance
   * @throws IOException If an input or output exception occurs
   */
  public void dump(AppointmentBook<Appointment> book) throws IOException {
    String owner = book.getOwnerName();

    this.writer.write(addEscapeCharacter(owner));
    this.writer.write(this.entryDelimiter);
  }

}
