package edu.pdx.cs410J.yl6;

import java.io.Reader;
import java.text.ParseException;
import java.io.IOException;

import edu.pdx.cs410J.ParserException;

/**
 * TextAppointmentParser is the class that given a <code>reader</code> instance
 * of {@link Reader} the method <code>parse</code> parses an {@link Appointment}
 * instance. Once the last character of the last field of
 * <code>Appointment</code> instance is parsed, the <code>reader</code> will not
 * read any character.
 * <p>
 * TextAppointmentParser is designed to be the base class such that any
 * appointment class that derives <code>Appointment</code> needs a parser from
 * plain text can derive this class, and override <code>parse</code> method.
 */
public class TextAppointmentParser {

  private Reader reader;
  private AppointmentValidator validator;
  private String[] appointmentArguments;
  private int currentArgIndex;
  private StringBuilder sb;

  private static final int expectedNumberofField = 3;

  static final String EOF_REACHED_PARSE_ARG = "End of file reached before the field been parsed completely";
  static final String NOT_ENOUGH_FIELD = "Not enough fields to build appointment from file";
  static final String CANNOT_FIND_FILE = "Cannot find file: ";
  static final String IOEXCEPTION_OCCUR = "IOException occurs during parsing with message: ";
  static final String PROGRAM_INTERNAL_ERROR = "Program internal error: ";
  static final String MORE_FIELD_THAN_NEEDED = "An extraneous field encountered to build appointment from file";

  public TextAppointmentParser(Reader reader, AppointmentValidator validator) {
    this.reader = reader;
    this.validator = validator;
    this.appointmentArguments = new String[expectedNumberofField];
    this.currentArgIndex = 0;
    this.sb = new StringBuilder();
  }

  public Appointment parse() throws ParserException, IOException {
    char c = ' ';
    int next;
    int count = 0;

    parsing: while ((next = reader.read()) != -1) {
      c = (char) next;
      count += 1;
      switch (c) {
        case '#':
          placeArgumentAndResetStringBuilder();
          this.currentArgIndex += 1;
          break;

        case '&':
          if (this.currentArgIndex < expectedNumberofField - 1) {
            throw new ParserException(
                NOT_ENOUGH_FIELD + " expect " + expectedNumberofField + ", but got " + (this.currentArgIndex + 1));
          }
          placeArgumentAndResetStringBuilder();
          this.currentArgIndex = 0;
          break parsing;

        case '\\':
          next = reader.read();
          if (next != -1) {
            this.sb.append((char) next);
          }
          break;

        default:
          this.sb.append(c);
      }
    }

    if (count == 0) {
      // nothing can be parsed 
      return null;
    }

    if (c != '&' || this.currentArgIndex != 0) {
      throw new ParserException(EOF_REACHED_PARSE_ARG);
    }

    if (!this.validator.isValid(this.appointmentArguments)) {
      throw new ParserException(this.validator.getErrorMessage());
    }

    try {
      return new Appointment(this.appointmentArguments[0], this.appointmentArguments[1], this.appointmentArguments[2]);
    } catch (ParseException e) {
      throw new ParserException(PROGRAM_INTERNAL_ERROR + e.getMessage());
    }
  }

  /**
   * Place the parsed string (stored in a string builder <code>sb</code>) into
   * <code>appointmentArguments</code> array which stores the arguments to be
   * passed into appointment's constructor, which is done in
   * <code>buildAppointment</code> method. And clear <code>sb</code> to store
   * intermediate results of the next field during parsing.
   * 
   * @throws ParserException
   */
  private void placeArgumentAndResetStringBuilder() throws ParserException {
    if (this.currentArgIndex == expectedNumberofField) {
      throw new ParserException(MORE_FIELD_THAN_NEEDED);
    }
    this.appointmentArguments[this.currentArgIndex] = this.sb.toString();
    this.sb.setLength(0);
  }

  public boolean hasMore() throws IOException {
    return this.reader.ready();
  }
}
