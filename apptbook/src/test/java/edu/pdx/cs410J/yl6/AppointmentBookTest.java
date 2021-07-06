package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

/**
 * Unit tests for the {@link AppointmentBook} class.
 *
 * You'll need to update these unit tests as you build out your program.
 */
public class AppointmentBookTest {

  @Test
  void owner() {
    AppointmentBook book = new AppointmentBook("a owner");
    assertThat(book.getOwnerName(), equalTo("a owner"));
  }

  @Test
  void addOneAppointment() {
    AppointmentBook book = new AppointmentBook("a owner");
    Appointment appointment = new Appointment("3/14/2020","4:29","3/14/2020","4:50","dummy");
    book.addAppointment(appointment);
    ArrayList<Appointment> toCompare = new ArrayList();
    toCompare.add(appointment);
    assertThat(book.getAppointments(), equalTo(toCompare));
  }

}
