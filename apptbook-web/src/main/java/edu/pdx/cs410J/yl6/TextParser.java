package edu.pdx.cs410J.yl6;

import java.io.IOException;

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.AppointmentBookParser;

public class TextParser implements AppointmentBookParser<AppointmentBook<Appointment>> {

  private static final String ZERO_APPOINTMENT_WITH_OWNER = "Cannot parse any appointment with owner ";
  static final String IOEXCEPTION_OCCUR = "IOException occurs during parsing with message: ";

  private TextAppointmentParser appointmentParser;
  private TextAppointmentBookParser appointmentBookParser;

  public TextParser(TextAppointmentBookParser appointmentBookParser,
      TextAppointmentParser appointmentParser) {
    this.appointmentBookParser = appointmentBookParser;
    this.appointmentParser = appointmentParser;
  }

  public AppointmentBook<Appointment> parse() throws ParserException {
    try {
      AppointmentBook<Appointment> book = this.appointmentBookParser.parse();
      if (!this.appointmentParser.hasMore()) {
        throw new ParserException(ZERO_APPOINTMENT_WITH_OWNER + book.getOwnerName());
      }
      Appointment appt;
      while ((appt = this.appointmentParser.parse()) != null) {
        book.addAppointment(appt);
      }
      return book;
    } catch (IOException e) {
      throw new ParserException(IOEXCEPTION_OCCUR + e.getMessage());
    }
  }
}
