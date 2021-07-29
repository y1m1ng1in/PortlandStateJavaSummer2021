package edu.pdx.cs410J.yl6;

import java.io.Writer;

/**
 * ParseableAppointmentDumper is the class that dumps the information of an
 * {@link Appointment} via a {@link Writer} specified by constructor.
 */
public class ParseableAppointmentDumper extends ParseableEntryDumper<Appointment> {

  protected Writer writer;

  /**
   * Create a ParseableAppointmentDumper instance
   * 
   * @param writer the {@link Writer} instance to be used in
   *               {@link ParseableAppointmentDumper#dump}
   */
  public ParseableAppointmentDumper(Writer writer) {
    super(writer);
  }

  /**
   * Return an array of strings in the order of each field of an appointment to be
   * dumped, which are appointment id followed by appointment description.
   * 
   * @param appointment the {@link Appointment} instance to be dumped
   * @return an array of strings in the order of each field of an appointment to
   *         be dumped
   */
  public String[] getStringFields(Appointment appointment) {
    return new String[] { appointment.getId(), appointment.getDescription() };
  }
}
