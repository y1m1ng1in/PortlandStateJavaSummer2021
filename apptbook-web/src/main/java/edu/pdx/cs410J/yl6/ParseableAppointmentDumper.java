package edu.pdx.cs410J.yl6;

import java.io.Writer;
import java.io.IOException;

/**
 * ParseableAppointmentDumper is the class that dumps the information of an
 * {@link Appointment} via a {@link Writer} specified by constructor.
 */
public class ParseableAppointmentDumper extends ParseableDumper<Appointment> {

  protected Writer writer;

  /**
   * Create a ParseableAppointmentDumper instance
   * 
   * @param writer the {@link Writer} instance to be used in
   *               {@link ParseableAppointmentDumper#dump}
   */
  public ParseableAppointmentDumper(Writer writer) {
    this.writer = writer;
  }

  /**
   * Dump an <code>appointment</code>
   * 
   * @param appointment the {@link Appointment} instance
   * @throws IOException If an input or output exception occurs
   */
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

  /**
   * Return an array of strings in the order of each field of an appointment to be
   * dumped.
   * 
   * @param appointment the {@link Appointment} instance to be dumped
   * @return an array of strings in the order of each field of an appointment to
   *         be dumped
   */
  private String[] getStringFields(Appointment appointment) {
    return new String[] { appointment.getBeginTimeString(), appointment.getEndTimeString(),
        appointment.getDescription() };
  }
}
