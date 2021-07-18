package edu.pdx.cs410J.yl6;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

import edu.pdx.cs410J.AbstractAppointment;

/**
 * Appointment is the class that store appointment information for a certain
 * appointment, which includes a description, begin date and time, end date and
 * time. The begin date and time, and end date and time can be any string, it
 * leaves the client program to specify a typical format for its uses.
 */
public class Appointment extends AbstractAppointment
    implements PlainTextRepresentable, PrettyPrintable, Comparable<Appointment> {

  private String beginString;
  private String endString;
  private String description;
  private Date begin;
  private Date end;
  static final int numberOfField = 3;

  /**
   * Create an appointment instance, where {@link SimpleDateFormat} is used to
   * parse string <code>begin</code> and <code>end</code> as begin and end time of
   * the appointment.
   * 
   * @param begin       a string that is parseable by
   *                    <code>SimpleDateFormat</code> in pattern
   *                    <code>"M/d/yyyy h:m a"</code> and before <code>end</code>
   * @param end         a string that is parsable by <code>SimpleDateFormat</code>
   *                    in pattern <code>"M/d/yyyy h:m a"</code> and after
   *                    <code>before</code>
   * @param description a nonempty string that describes the appointment
   * @throws ParseException the <code>begin</code> and <code>end</code> cannot be
   *                        parsed by <code>SimpleDateFormat</code> successfully.
   */
  public Appointment(String begin, String end, String description) throws ParseException {
    DateFormat df = new SimpleDateFormat("M/d/yyyy h:m a");

    df.setLenient(false);
    this.begin = df.parse(begin);
    this.end = df.parse(end);

    this.beginString = begin;
    this.endString = end;
    this.description = description;
  }

  /**
   * Returns a String describing the beginning date and time of this appointment.
   * 
   * @return a string describing the beginning date and time of this appointment.
   */
  @Override
  public String getBeginTimeString() {
    return DateFormat.getInstance().format(this.begin);
  }

  /**
   * Returns a String describing the ending date and time of this appointment.
   * 
   * @return a string describing the ending date and time of this appointment.
   */
  @Override
  public String getEndTimeString() {
    return DateFormat.getInstance().format(this.end);
  }

  /**
   * Returns a description of this appointment (for instance,
   * <code>"Have coffee with Marsha"</code>).
   * 
   * @return a string of the description of the appointment
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * Get an array of strings that will be dumped to plain text file in the order
   * from low index to high index of the returned array. The delimiter between
   * fields is determined by the invoking dumper class.
   * 
   * @return an array of strings that are to be dumped to plain text where the
   *         order is same as from low index to high index of this array
   */
  @Override
  public String[] getStringFields() {
    String[] fields = new String[numberOfField];
    fields[0] = this.beginString;
    fields[1] = this.endString;
    fields[2] = this.description;
    return fields;
  }

  /**
   * Get an integer that represents the number of fields is expected to be parsed
   * from plain text file
   * 
   * @return an integer that represents the number of fields is expected to be
   *         parsed from plain text file
   */
  @Override
  public int getExpectedNumberOfField() {
    return numberOfField;
  }

  /**
   * Returns the {@link Date} that this appointment begins.
   */
  public Date getBeginTime() {
    return this.begin;
  }

  /**
   * Returns the {@link Date} that this appointment ends.
   */
  public Date getEndTime() {
    return this.end;
  }

  /**
   * Compares invoking appointment with appointment <code>appt</code> passed in.
   * If both begin time and end time are same between two appointments, then two
   * appointments are ordered by description lexicographically. Otherwise, if
   * begin time between two are same, then ordered by end time; otherwise, ordered
   * by their begin time.
   * 
   * @param appt the <code>Appointment</code> instance that is to be compared with
   *             invoking appointment.
   * @return 1 if invoking appointment is ordered after parameter; 0 if two
   *         appointments are same (which means both begin, end time, and
   *         description are same) -1 if invoking appointment is ordered before
   *         parameter.
   */
  @Override
  public int compareTo(Appointment appt) {
    if (this.begin.equals(appt.begin) && this.end.equals(appt.end)) {
      return this.description.compareTo(appt.description);
    }
    if (this.begin.equals(appt.begin)) {
      return this.end.compareTo(appt.end);
    }
    return this.begin.compareTo(appt.begin);
  }

  /**
   * Get an arrya of strings that stores strings to be pretty print to standard
   * output or file.
   * 
   * @return an array of strings that stores strings to be pretty print to
   *         standard output or file.
   */
  @Override
  public String[] getPrettyPrinterFields() {
    String[] fields = new String[numberOfField + 1];
    fields[0] = getBeginTimeString();
    fields[1] = getEndTimeString();
    fields[2] = this.description;

    long duration = (int) ((this.end.getTime() - this.begin.getTime()) / 60000);
    if (duration <= 1) {
      fields[3] = String.valueOf(duration) + " minute";
    } else {
      fields[3] = String.valueOf(duration) + " minutes";
    }

    return fields;
  }

}
