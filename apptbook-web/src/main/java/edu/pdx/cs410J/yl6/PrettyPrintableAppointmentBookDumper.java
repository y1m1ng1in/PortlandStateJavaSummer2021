package edu.pdx.cs410J.yl6;

import java.io.IOException;
import java.io.Writer;

public class PrettyPrintableAppointmentBookDumper extends TabularDumper<AppointmentBook<Appointment>> {

  protected Writer writer;
  
  public PrettyPrintableAppointmentBookDumper(Writer writer) {
    this.writer = writer;
  }

  public void dump(AppointmentBook<Appointment> book) throws IOException {
    this.writer.write(formatFieldName("Owner"));
    this.writer.write(formatField(book.getOwnerName()));
    this.writer.write("\n");
    this.writer.write(border);
  }
  
}
