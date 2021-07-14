package edu.pdx.cs410J.yl6;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.PrintStream;
import java.util.Collection;

import edu.pdx.cs410J.AppointmentBookDumper;
import edu.pdx.cs410J.AbstractAppointment;
import edu.pdx.cs410J.AbstractAppointmentBook;

public class PrettyPrinter<T extends AbstractAppointmentBook, 
                           E extends AbstractAppointment & PrettyPrintable> 
    implements AppointmentBookDumper<T> {
  
  private Writer writer;

  public PrettyPrinter(PrintStream writer) {
    this.writer = new PrintWriter(writer);
  }

  public PrettyPrinter(Writer writer) {
    this.writer = new PrintWriter(writer);
  }

  public void dump(T book) throws IOException {
    Collection<E> appts = book.getAppointments();
    
    for (E appt : appts) {
      String[] appointmentFields = appt.getPrettyPrinterFields();
      for (int i = 0; i < appointmentFields.length; ++i) {
        this.writer.write(appointmentFields[i]);
        this.writer.write(" ");
      }
      this.writer.write("\n");
    }
    this.writer.flush();
    this.writer.close();
  } 
}