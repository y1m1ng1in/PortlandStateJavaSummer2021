package edu.pdx.cs410J.yl6;

import java.io.Writer;

public class ParseableAppointmentSlotDumper extends ParseableEntryDumper<AppointmentSlot> {

  protected Writer writer;

  /**
   * Create a ParseableAppointmentSlotDumper instance
   * 
   * @param writer the {@link Writer} instance to be used in
   *               {@link ParseableAppointmentSlotDumper#dump}
   */
  public ParseableAppointmentSlotDumper(Writer writer) {
    super(writer);
  }

  /**
   * Return an array of strings in the order of each field of an appointment slot
   * to be dumped, which are appointment id followed by begin time and end time.
   * 
   * @param appointment the {@link Appointment} instance to be dumped
   * @return an array of strings in the order of each field of an appointment slot
   *         to be dumped
   */
  public String[] getStringFields(AppointmentSlot appointment) {
    return new String[] { appointment.getBeginTimeString(), appointment.getEndTimeString(), appointment.getId() };
  }
}
