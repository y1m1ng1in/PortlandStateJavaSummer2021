package edu.pdx.cs410J.yl6;

import java.io.Reader;
import java.io.IOException;

import edu.pdx.cs410J.ParserException;

/**
 * TextAppointmentBookParser is the class that given a <code>reader</code>
 * instance of {@link Reader} the method <code>parse</code> parses an empty
 * {@link AppointmentBook} instance (an appointment book with 0 appointment).
 * Once the owner of the <code>AppointmentBook</code> (which is the only field
 * followed by a collection of <code>Appointment</code>) is parsed, the
 * <code>reader</code> will not read any character.
 * <p>
 * TextAppointmentBookParser is designed to be the base class such that any
 * appointment book class that derives <code>AppointmentBook</code> needs a
 * parser from plain text can derive this class, and override <code>parse</code>
 * method.
 */
public class TextAppointmentBookParser {

  private Reader reader;
  private NonemptyStringValidator ownerValidator;
  private StringBuilder sb;

  static final String PROHIBIT_CHAR_IN_OWNER = "Prohibited character # occurs when parsing owner name.";
  static final String EOF_REACHED_PARSE_OWNER = "End of file reached before owner been parsed completely";

  /**
   * Creates a TextAppointmentBookParser instance
   * 
   * @param reader the <code>Reader</code> instance to be used to perfrom read operation
   */
  public TextAppointmentBookParser(Reader reader) {
    this.reader = reader;
    this.ownerValidator = new NonemptyStringValidator("owner");
    this.sb = new StringBuilder();
  }

  /**
   * Parse owner of the appointment book from input file using given
   * <code>reader</code> from constructor of the instance, then creates an empty
   * appointment book.
   * 
   * @return an empty appointment book with owner get parsed
   * @throws IOException     exception raise duing to read from file
   * @throws ParserException incorrect format of the file (delimiter '#' occurred
   *                         without escaped from '\'; or cannot completely parse
   *                         the owner name, which means '&amp;' does not occur until
   *                         end-of-file reached)
   */
  public AppointmentBook<Appointment> parse() throws ParserException, IOException {
    char c = ' ';
    int next;

    parsing: while ((next = reader.read()) != -1) {
      c = (char) next;
      switch (c) {
        case '&':
          break parsing;

        case '#': // '#' must be escaped by '\#'
          throw new ParserException(PROHIBIT_CHAR_IN_OWNER);

        case '\\':
          next = reader.read();
          if (next != -1) {
            c = (char) next;
          }

        default:
          this.sb.append(c);
      }
    }

    if (c != '&') {
      throw new ParserException(EOF_REACHED_PARSE_OWNER);
    }

    String owner = this.sb.toString();

    if (!this.ownerValidator.isValid(owner)) {
      throw new ParserException(this.ownerValidator.getErrorMessage());
    }

    return new AppointmentBook<>(owner);
  }
}
