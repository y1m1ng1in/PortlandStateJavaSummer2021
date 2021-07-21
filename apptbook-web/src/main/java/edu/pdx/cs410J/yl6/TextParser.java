package edu.pdx.cs410J.yl6;

import java.io.IOException;

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.AppointmentBookParser;

/**
 * TextParser is the class that takes {@link TextAppointmentBookParser} and
 * {@link TextAppointmentParser} or their subclasses to parse an
 * {@link AppointmentBook}. This class do <strong>not</strong> implements
 * anything related to parsing a specific content, this class simply invokes
 * {@link TextAppointmentBookParser#parse} and
 * {@link TextAppointmentParser#parse} method.
 * <p>
 * This class uses <code>TextAppointmentBookParser</code> first, then
 * iteratively invokes <code>parse</code> of <code>TextAppointmentParser</code>
 * until end-of-file reached.
 */
public class TextParser implements AppointmentBookParser<AppointmentBook<Appointment>> {

  private static final String ZERO_APPOINTMENT_WITH_OWNER = "No appointment associated with owner ";
  static final String IOEXCEPTION_OCCUR = "IOException occurs during parsing with message: ";

  private TextAppointmentParser appointmentParser;
  private TextAppointmentBookParser appointmentBookParser;

  /**
   * Create a TextParser instance
   * 
   * @param appointmentBookParser the parser of appointment book information,
   *                              usually the content to be parsed is the meta
   *                              information of the book
   * @param appointmentParser     the parser of individual appointment
   */
  public TextParser(TextAppointmentBookParser appointmentBookParser, TextAppointmentParser appointmentParser) {
    this.appointmentBookParser = appointmentBookParser;
    this.appointmentParser = appointmentParser;
  }

  /**
   * Parse an appointment book with its appointments
   * 
   * @return an <code>AppointmentBook</code> instance
   * @throws ParserException If parser throw any <code>ParserException</code>, or
   *                         no appointment get parsed
   */
  public AppointmentBook<Appointment> parse() throws ParserException {
    int added = 0;
    try {
      AppointmentBook<Appointment> book = this.appointmentBookParser.parse();
      Appointment appt;
      while ((appt = this.appointmentParser.parse()) != null) {
        book.addAppointment(appt);
        added += 1;
      }
      if (added == 0) {
        throw new ParserException(ZERO_APPOINTMENT_WITH_OWNER + book.getOwnerName());
      }
      return book;
    } catch (IOException e) {
      throw new ParserException(IOEXCEPTION_OCCUR + e.getMessage());
    }
  }
}
