package edu.pdx.cs410J.yl6;

import java.util.Collection;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;

import edu.pdx.cs410J.AppointmentBookDumper;
import edu.pdx.cs410J.AbstractAppointment;
import edu.pdx.cs410J.AbstractAppointmentBook;

public class TextDumper<T extends AbstractAppointmentBook, E extends AbstractAppointment> 
    implements AppointmentBookDumper<T> {
  private String filename;

  public TextDumper(String filename) {
    this.filename = filename;
  }

  @Override
  public void dump(T book) throws IOException {
    try {
      Writer writer = new FileWriter(this.filename);
      Collection<E> appts = book.getAppointments();
      String owner = book.getOwnerName();
      writer.write(owner);
      writer.write('&');
      for (E appt: appts) {
        String begin = appt.getBeginTimeString();
        String end = appt.getEndTimeString();
        String description = appt.getDescription();
        writer.write(begin);
        writer.write('#');
        writer.write(end);
        writer.write('#');
        writer.write(description);
        writer.write('&');
      }
      writer.flush();
      writer.close();
    } catch (IOException ex) {
      throw ex;
    }
  }
}