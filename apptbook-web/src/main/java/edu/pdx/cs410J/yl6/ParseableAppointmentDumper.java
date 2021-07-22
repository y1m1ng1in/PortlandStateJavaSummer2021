package edu.pdx.cs410J.yl6;

import java.io.Writer;
import java.io.IOException;

public class ParseableAppointmentDumper extends ParseableDumper<Appointment> {
  
  protected Writer writer;

  public ParseableAppointmentDumper(Writer writer) {
    this.writer = writer;
  }

  public void dump(Appointment appointment) throws IOException {
    String[] appointmentFields = getStringFields(appointment);

    for (int i = 0; i < appointmentFields.length; ++i) {
      this.writer.write(addEscapeCharacter(appointmentFields[i]));
      if (i + 1 == appointmentFields.length) {
        this.writer.write(this.entryDelimiter);
      } else {
        this.writer.write(this.fieldDelimiter);
      }
    }
  }

  private String[] getStringFields(Appointment appointment) {
    return new String[] { appointment.getBeginTimeString(), appointment.getEndTimeString(),
        appointment.getDescription() };
  }
}
