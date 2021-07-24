package edu.pdx.cs410J.yl6;

import java.io.Writer;

public class ParseableAppointmentDumper extends ParseableEntryDumper<Appointment> {
  
  protected Writer writer;

  public ParseableAppointmentDumper(Writer writer) {
    super(writer);
  }

  public String[] getStringFields(Appointment appointment) {
    return new String[] { appointment.getBeginTimeString(), appointment.getEndTimeString(),
        appointment.getDescription() };
  }
}
