package edu.pdx.cs410J.yl6;

import java.io.Writer;
import java.io.IOException;

public class ParseableAppointmentBookDumper extends ParseableDumper<AppointmentBook<Appointment>> {

  protected Writer writer;

  public ParseableAppointmentBookDumper(Writer writer) {
    this.writer = writer;
  }

  public void dump(AppointmentBook<Appointment> book) throws IOException {
    String owner = book.getOwnerName();

    this.writer.write(addEscapeCharacter(owner));
    this.writer.write(this.entryDelimiter);
  }

}
