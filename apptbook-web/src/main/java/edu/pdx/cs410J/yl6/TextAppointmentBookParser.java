package edu.pdx.cs410J.yl6;

import java.io.Reader;

import edu.pdx.cs410J.ParserException;

public class TextAppointmentBookParser extends Parser<AppointmentBook<Appointment>> {

  final private int numberofField = 1;

  private NonemptyStringValidator ownerValidator; 

  /**
   * Creates a TextAppointmentBookParser instance
   * 
   * @param reader the <code>Reader</code> instance to be used to perfrom read
   *               operation
   */
  public TextAppointmentBookParser(Reader reader) {
    this.reader = reader;
    this.ownerValidator = new NonemptyStringValidator("owner");
  }

  @Override
  public int getExpectedNumberofField() {
    return numberofField;
  }

  @Override
  public AppointmentBook<Appointment> instantiate(String... fields) throws ParserException {
    if (!ownerValidator.isValid(fields[0])) {
      throw new ParserException(ownerValidator.getErrorMessage());
    }
    return new AppointmentBook<>(fields[0]);
  }
}
