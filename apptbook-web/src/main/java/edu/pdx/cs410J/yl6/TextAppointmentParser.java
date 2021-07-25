package edu.pdx.cs410J.yl6;

import java.io.Reader;

import edu.pdx.cs410J.ParserException;

public class TextAppointmentParser extends Parser<Appointment> {

  final private int numberofField = 3;
  private AppointmentValidator validator;

  /**
   * Create a TextAppointmentParser instance
   * 
   * @param reader    the {@link Reader} instance to read in data to be parsed as
   *                  an {@link Appointment}, usually <code>reader</code> is the
   *                  same reference to the one that passed into
   *                  {@link TextAppointmentBookParser} instance such that
   *                  {@link TextParser} can use the same <code>reader</code> to
   *                  parse a complete appointment book
   * @param validator an {@link AppointmentValidator} instance that checks if a
   *                  parsed appointment is valid
   */
  public TextAppointmentParser(Reader reader, AppointmentValidator validator) {
    this.reader = reader;
    this.validator = validator;
  }

  @Override
  public int getExpectedNumberofField() {
    return numberofField;
  }

  @Override
  public Appointment instantiate(String... fields) throws ParserException {
    Appointment appointment = this.validator.createAppointmentFromString(fields[0], fields[1], fields[2]);
    if (appointment == null) {
      throw new ParserException(this.validator.getErrorMessage());
    }
    return appointment;
  }

}
