package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link Appointment} class.
 *
 * You'll need to update these unit tests as you build out your program.
 */
public class AppointmentTest {

  @Test
  void getBeginTimeStringNeedsToBeImplemented() {
    Appointment appointment = new Appointment("3/14/2020","4:29","3/14/2020","4:50","dummy");
    assertThat(appointment.getBeginTimeString(), equalTo("3/14/2020 4:29"));
  }

  @Test
  void initiallyAllAppointmentsHaveTheSameDescription() {
    Appointment appointment = new Appointment("3/14/2020","4:29","3/14/2020","4:50","dummy");
    assertThat(appointment.getDescription(), equalTo("dummy"));
  }

  @Test
  void forProject1ItIsOkayIfGetBeginTimeReturnsNull() {
    Appointment appointment = new Appointment("3/14/2020","4:29","3/14/2020","4:50","dummy");
    assertThat(appointment.getBeginTime(), is(nullValue()));
  }

}
