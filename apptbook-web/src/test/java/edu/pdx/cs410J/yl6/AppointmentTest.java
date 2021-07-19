package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import edu.pdx.cs410J.ParserException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppointmentTest {

  /**
   * Tests that correct begin date and time is returned as a string.
   */
  @Test
  void getBeginTimeStringNeedsToBeImplemented() throws ParseException {
    Appointment appointment = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    assertThat(appointment.getBeginTimeString(), equalTo("3/14/20, 4:29 PM"));
  }

  /**
   * Tests that correct end date and time is returned as a string.
   */
  @Test
  void getEndTimeStringNeedsToBeImplemented() throws ParseException {
    Appointment appointment = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    assertThat(appointment.getEndTimeString(), equalTo("3/14/20, 4:50 PM"));
  }

  /**
   * Tests that correct description is returned as a string
   */
  @Test
  void getDescription() throws ParseException {
    Appointment appointment = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    assertThat(appointment.getDescription(), equalTo("dummy"));
  }

  /**
   * Tests that get begin time returns null since not implemented.
   */
  @Test
  void forProject3ItIsOkayIfGetBeginTimeReturnsNull() throws ParseException {
    DateFormat df = new SimpleDateFormat("M/d/yyyy h:m a");
    Date d = df.parse("3/14/2020 4:29 pm");
    Appointment appointment = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    assertThat(appointment.getBeginTime(), equalTo(d));
  }

  @Test
  void testCompareToGivenSameAppointment() throws ParseException {
    Appointment appointment1 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    Appointment appointment2 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    assertThat(appointment1.compareTo(appointment2), equalTo(0));
  }

  @Test
  void testCompareToGivenSameBeginAndSameEnd() throws ParseException {
    Appointment appointment1 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummya");
    Appointment appointment2 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummyb");
    assertThat(appointment1.compareTo(appointment2), equalTo(-1));
  }

  @Test
  void testCompareToGivenSameBegin() throws ParseException {
    Appointment appointment1 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummya");
    Appointment appointment2 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:5 pm","dummyb");
    assertThat(appointment1.compareTo(appointment2), equalTo(1));
  }

  @Test
  void testCompareToWithDifferentBeginAndSameEndDescription() throws ParseException {
    Appointment appointment1 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    Appointment appointment2 = new Appointment("3/14/2020 3:29 pm","3/14/2020 4:50 pm","dummy");
    assertThat(appointment1.compareTo(appointment2), equalTo(1));
  }

  @Test
  void testCompareToWithDifferentBegin() throws ParseException {
    Appointment appointment1 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    Appointment appointment2 = new Appointment("3/14/2020 3:29 am","3/14/2020 6:50 pm","dum");
    assertThat(appointment1.compareTo(appointment2), equalTo(1));
  }

  @Test
  void getPrettyPrinterFieldsTestCase1() throws ParseException {
    Appointment appointment = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    String[] pretty = appointment.getPrettyPrinterFields();
    assertThat(pretty[0], equalTo("3/14/20, 4:29 PM"));
    assertThat(pretty[1], equalTo("3/14/20, 4:50 PM"));
    assertThat(pretty[2], equalTo("dummy"));
    assertThat(pretty[3], equalTo("21 minutes"));
  }

  @Test
  void getPrettyPrinterFieldsTestCase2() throws ParseException {
    Appointment appointment = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:30 pm","dummy");
    String[] pretty = appointment.getPrettyPrinterFields();
    assertThat(pretty[0], equalTo("3/14/20, 4:29 PM"));
    assertThat(pretty[1], equalTo("3/14/20, 4:30 PM"));
    assertThat(pretty[2], equalTo("dummy"));
    assertThat(pretty[3], equalTo("1 minute"));
  }

  @Test
  void getBeginTimeTest() throws ParseException {
    Appointment appointment = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:30 pm","dummy");
    Calendar myCalendar = new GregorianCalendar(2020, Calendar.MARCH, 14, 16, 29);
    Date myDate = myCalendar.getTime();
    assertThat(appointment.getBeginTime(), equalTo(myDate));
  }

  @Test
  void getEndTimeTest() throws ParseException {
    Appointment appointment = new Appointment("3/14/2020 4:29 pm","12/14/2020 7:30 pm","dummy");
    Calendar myCalendar = new GregorianCalendar(2020, Calendar.DECEMBER, 14, 19, 30);
    Date myDate = myCalendar.getTime();
    assertThat(appointment.getEndTime(), equalTo(myDate));
  }

}

