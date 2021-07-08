package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

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
  void getBeginTimeStringNeedsToBeImplemented() {
    Appointment appointment = new Appointment("3/14/2020","4:29","3/14/2020","4:50","dummy");
    assertThat(appointment.getBeginTimeString(), equalTo("3/14/2020 4:29"));
  }

  /**
   * Tests that correct end date and time is returned as a string.
   */
  @Test
  void getEndTimeStringNeedsToBeImplemented() {
    Appointment appointment = new Appointment("3/14/2020","4:29","3/15/2020","4:50","dummy");
    assertThat(appointment.getEndTimeString(), equalTo("3/15/2020 4:50"));
  }

  /**
   * Tests that correct description is returned as a string
   */
  @Test
  void getDescription() {
    Appointment appointment = new Appointment("3/14/2020","4:29","3/14/2020","4:50","dummy");
    assertThat(appointment.getDescription(), equalTo("dummy"));
  }

  /**
   * Tests that get begin time returns null since not implemented.
   */
  @Test
  void forProject2ItIsOkayIfGetBeginTimeReturnsNull() {
    Appointment appointment = new Appointment("3/14/2020","4:29","3/14/2020","4:50","dummy");
    assertThat(appointment.getBeginTime(), is(nullValue()));
  }

}
