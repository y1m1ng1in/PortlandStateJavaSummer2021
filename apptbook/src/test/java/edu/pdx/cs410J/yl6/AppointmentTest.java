package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import edu.pdx.cs410J.ParserException;
import java.text.ParseException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link Appointment} class.
 */
public class AppointmentTest {

  /**
   * Tests that correct begin date and time is returned as a string.
   */
  @Test
  void getBeginTimeStringNeedsToBeImplemented() throws ParseException, AppointmentInvalidFieldException {
    Appointment appointment = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    assertThat(appointment.getBeginTimeString(), equalTo("3/14/2020 4:29 pm"));
  }

  /**
   * Tests that correct end date and time is returned as a string.
   */
  @Test
  void getEndTimeStringNeedsToBeImplemented() throws ParseException, AppointmentInvalidFieldException {
    Appointment appointment = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    assertThat(appointment.getEndTimeString(), equalTo("3/14/2020 4:50 pm"));
  }

  /**
   * Tests that correct description is returned as a string
   */
  @Test
  void getDescription() throws ParseException, AppointmentInvalidFieldException {
    Appointment appointment = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    assertThat(appointment.getDescription(), equalTo("dummy"));
  }

  /**
   * Tests that get begin time returns null since not implemented.
   */
  @Test
  void forProject2ItIsOkayIfGetBeginTimeReturnsNull() throws ParseException, AppointmentInvalidFieldException {
    DateFormat df = new SimpleDateFormat("M/d/yyyy h:m a");
    Date d = df.parse("3/14/2020 4:29 pm");
    Appointment appointment = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    assertThat(appointment.getBeginTime(), equalTo(d));
  }

  @Test
  void constructorException() throws ParseException, AppointmentInvalidFieldException {
    Exception exception = assertThrows(AppointmentInvalidFieldException.class, 
        () -> new Appointment("22/2/2020 2:22 pm","3/14/2020 4:50 pm","dummy"));
  }

}
