package edu.pdx.cs410J.yl6;

import java.io.Reader;
import java.util.Date;

import edu.pdx.cs410J.ParserException;

public class TextAppointmentSlotParser extends Parser<AppointmentSlot> {
  
  final private int numberofField = 3;

  public TextAppointmentSlotParser(Reader reader) {
    this.reader = reader;
  }

  @Override
  public int getExpectedNumberofField() {
    return numberofField;
  }

  @Override
  public AppointmentSlot instantiate(String... fields) throws ParserException {
    Date begin = Helper.validateAndParseDate(fields[0]);
    Date end = Helper.validateAndParseDate(fields[1]);
    if (!Helper.validateAndGetDateInterval(begin, end, "begin time of appointment slot", "end time of appointment slot")) {
      throw new ParserException(Helper.getErrorMessage());
    }
    return new AppointmentSlot(fields[2], begin, end);
  }
}
