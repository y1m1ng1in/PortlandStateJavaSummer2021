package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.TreeSet;
import java.text.ParseException;

/**
 * Unit tests for the {@link AppointmentBook} class.
 */
public class AppointmentBookTest {

  /**
   * Tests that correct owner is returned as a string.
   */
  @Test
  void owner() {
    AppointmentBook book = new AppointmentBook("a owner");
    assertThat(book.getOwnerName(), equalTo("a owner"));
  }

  /**
   * Tests that add one appointment to the book, and get appointments correctly
   * return a collection of appointments with size 1 with same added content.
   */
  @Test
  void addOneAppointment() throws ParseException {
    AppointmentBook book = new AppointmentBook("a owner");
    Appointment appointment = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy");
    book.addAppointment(appointment);
    TreeSet<Appointment> toCompare = new TreeSet();
    toCompare.add(appointment);
    assertThat(book.getAppointments(), equalTo(toCompare));
  }

  /**
   * Tests that add 2 appointments to the book, and get appointments correctly
   * return a collection of appointments with size 2 with same added content.
   */
  @Test
  void addTwoAppointments() throws ParseException {
    AppointmentBook book = new AppointmentBook("a owner");
    Appointment appointment1 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy1");
    Appointment appointment2 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy2");
    book.addAppointment(appointment1);
    book.addAppointment(appointment2);
    TreeSet<Appointment> toCompare = new TreeSet();
    toCompare.add(appointment1);
    toCompare.add(appointment2);
    assertThat(book.getAppointments(), equalTo(toCompare));
  }

  /**
   * Tests that add 3 appointments to the book, and get appointments correctly
   * return a collection of appointments with size 3 with same added content.
   */
  @Test
  void addThreeAppointments() throws ParseException {
    AppointmentBook book = new AppointmentBook("a owner");
    Appointment appointment1 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy1");
    Appointment appointment2 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy2");
    Appointment appointment3 = new Appointment("3/14/2020 4:29 pm","3/14/2020 4:50 pm","dummy3");
    book.addAppointment(appointment1);
    book.addAppointment(appointment2);
    book.addAppointment(appointment3);
    TreeSet<Appointment> toCompare = new TreeSet();
    toCompare.add(appointment1);
    toCompare.add(appointment2);
    toCompare.add(appointment3);
    assertThat(book.getAppointments(), equalTo(toCompare));
  }

}
