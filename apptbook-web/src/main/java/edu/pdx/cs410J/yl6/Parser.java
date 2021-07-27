package edu.pdx.cs410J.yl6;

import java.io.IOException;
import java.io.Reader;

import edu.pdx.cs410J.ParserException;

/**
 * Parser is the class that implements a "template method" <code>parse</code>
 * that parses an instance of <code>T</code> via a {@link Reader}. Given the
 * expected number of fields to be parsed for constructing <code>T</code> via
 * {@link Parser#getExpectedNumberofField}, and the method to instantiate
 * <code>T</code> by strings via {@link Parser#instantiate}, <code>parse</code>
 * reads character by character until an entry delimiter encountered.
 */
public abstract class Parser<T> {

  static final String EOF_REACHED_PARSE_ARG = "End of file reached before the field been parsed completely";
  static final String NOT_ENOUGH_FIELD = "Not enough fields to build appointment from file";
  static final String MORE_FIELD_THAN_NEEDED = "An extraneous field encountered to build appointment from file";
  static final String PROHIBIT_CHAR_IN_OWNER = "Prohibited character # occurs when parsing owner name.";

  protected final char fieldDelimiter = '#';
  protected final char entryDelimiter = '&';
  protected final char escapeCharacter = '\\';

  private int currentArgIndex = 0;
  private StringBuilder sb = new StringBuilder();
  private String[] argumetStrings = new String[getExpectedNumberofField()];

  protected Reader reader;

  /**
   * Returns the number of fields expected to be parsed before an entry delimiter
   * encountered.
   * 
   * @return the number of fields expected to be parsed
   */
  public abstract int getExpectedNumberofField();

  /**
   * Instantiate <code>T</code> given <code>arguments</code>
   * 
   * @param arguments strings that are used to construct <code>T</code>
   * @return an instance of <code>T</code>
   * @throws ParserException If any error occurs during parsing
   */
  public abstract T instantiate(String... arguments) throws ParserException;

  /**
   * The template method for parsing a <code>T</code>
   * 
   * @return an instance of <code>T</code>
   * @throws ParserException If any error occurs during parsing
   * @throws IOException     If an input or output exception occurs
   */
  public T parse() throws ParserException, IOException {
    char c = ' ';
    int next;
    int count = 0;

    int expectedNumberofField = getExpectedNumberofField();

    parsing: while ((next = reader.read()) != -1) {
      c = (char) next;
      count += 1;
      switch (c) {
      case fieldDelimiter:
        if (expectedNumberofField == 1) {
          // '#' must be escaped by '\#'
          throw new ParserException(PROHIBIT_CHAR_IN_OWNER);
        }
        placeArgumentAndResetStringBuilder();
        this.currentArgIndex += 1;
        break;

      case entryDelimiter:
        if (this.currentArgIndex < expectedNumberofField - 1) {
          throw new ParserException(
              NOT_ENOUGH_FIELD + " expect " + expectedNumberofField + ", but got " + (this.currentArgIndex + 1));
        }
        placeArgumentAndResetStringBuilder();
        this.currentArgIndex = 0;
        break parsing;

      case escapeCharacter:
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

    if (c != entryDelimiter || this.currentArgIndex != 0) {
      throw new ParserException(EOF_REACHED_PARSE_ARG);
    }

    return instantiate(this.argumetStrings);
  }

  /**
   * Place the parsed string (stored in a string builder <code>sb</code>) into
   * <code>appointmentArguments</code> array which stores the arguments to be
   * passed into appointment's constructor, which is done in
   * <code>buildAppointment</code> method. And clear <code>sb</code> to store
   * intermediate results of the next field during parsing.
   * 
   * @throws ParserException If an extraneous field detected
   */
  private void placeArgumentAndResetStringBuilder() throws ParserException {
    int expectedNumberofField = getExpectedNumberofField();

    if (this.currentArgIndex == expectedNumberofField) {
      throw new ParserException(MORE_FIELD_THAN_NEEDED);
    }
    this.argumetStrings[this.currentArgIndex] = this.sb.toString();
    this.sb.setLength(0);
  }

}
