package edu.pdx.cs410J.yl6;

import java.io.Writer;
import java.io.IOException;

import edu.pdx.cs410J.AppointmentBookDumper;

/**
 * TextDumper encapsulates method <code>dump</code> which writes owner of
 * appointment book to specified file, followed by each appointment's fields
 * delimited by '#', each appointment entry is delimited by '&amp;'. Between the
 * owner and the first appointment written, it is delimited by '&amp;', too.
 * <p>
 * TextDumper also detects any character that conflicts with delimiters, thus it
 * adds '\' before any conflicted character, also adds '\' before '\'.
 */
public class TextDumper implements AppointmentBookDumper<AppointmentBook<Appointment>> {

  private Writer writer;

  /**
   * Create a TextDumper instance with specified relative path of the file to be
   * written.
   * 
   * @param writer the {@link Writer} instance that is to be used to write
   */
  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  /**
   * Dump an appointment book
   * 
   * @param book the appointment book to be dumped
   * @throws IOException If an input or output exception occurs
   */
  public void dump(AppointmentBook<Appointment> book) throws IOException {
    ParseableAppointmentBookDumper bookDumper = new ParseableAppointmentBookDumper(writer);
    ParseableAppointmentDumper appointmentDumper = new ParseableAppointmentDumper(writer);
    Dumper.dumpAppointmentBook(book, bookDumper, appointmentDumper);
  }

}