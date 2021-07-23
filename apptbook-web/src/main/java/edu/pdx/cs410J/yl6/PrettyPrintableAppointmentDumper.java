package edu.pdx.cs410J.yl6;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.text.DateFormat;

public class PrettyPrintableAppointmentDumper extends TabularDumper<Appointment> {

  protected Writer writer;

  public PrettyPrintableAppointmentDumper(Writer writer) {
    this.writer = writer;
  }

  public void dump(Appointment appointment) throws IOException {
    String[] appointmentFields = getFields(appointment);
    String[] fieldNames = getFieldNames();
    
    for (int i = 0; i < appointmentFields.length; ++i) {
      this.writer.write(formatFieldName(fieldNames[i]));
      this.writer.write(formatField(appointmentFields[i]));
      this.writer.write("\n");
    }
    this.writer.write(border);
  }

  protected String[] getFieldNames() {
    return new String[] { "Begin at", "End at", "Description", "Duration" };
  }

  /**
   * Get an arrya of strings that stores strings to be pretty print to standard
   * output or file.
   * 
   * @param appointment the appointment to be pretty printed
   * @return an array of strings that stores strings to be pretty print to
   *         standard output or file.
   */
  protected String[] getFields(Appointment appointment) {
    Date begin = appointment.getBeginTime();
    Date end = appointment.getEndTime();
    String[] fields = new String[4];

    fields[0] = DateFormat.getInstance().format(begin);
    fields[1] = DateFormat.getInstance().format(end);
    fields[2] = appointment.getDescription();

    long duration = (int) ((end.getTime() - begin.getTime()) / 60000);
    if (duration <= 1) {
      fields[3] = String.valueOf(duration) + " minute";
    } else {
      fields[3] = String.valueOf(duration) + " minutes";
    }

    return fields;
  }

}
