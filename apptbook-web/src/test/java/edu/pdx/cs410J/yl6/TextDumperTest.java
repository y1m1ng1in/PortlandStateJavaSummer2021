package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.text.ParseException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import edu.pdx.cs410J.ParserException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

public class TextDumperTest {

  Date getDate(String s) throws ParseException {
    DateFormat df = new SimpleDateFormat("M/d/yyyy h:m a");
    df.setLenient(false);
    return df.parse(s);
  }

  @Test
  void writeOneAppt() throws IOException, ParseException {
    Appointment appt = new Appointment(
        getDate("12/12/2019 12:52 am"), getDate("4/5/2020 2:52 pm"), "A description");
    AppointmentBook book = new AppointmentBook("yml");
    book.addAppointment(appt);
    StringWriter fw = new StringWriter();
    TextDumper td = new TextDumper(fw);
    td.dump(book);
    String exp = "yml&12/12/2019 12:52 AM#4/5/2020 2:52 PM#A description&";
    assertThat(exp, equalTo(fw.toString()));
  }

  @Test
  void writeThreeAppt() throws IOException, ParseException {
    Appointment appt = new Appointment(
        getDate("12/12/2020 12:52 pm"), getDate("4/5/2021 2:52 am"), "C");
    Appointment appt1 = new Appointment(
        getDate("12/12/2020 12:52 pm"), getDate("4/5/2021 2:52 am"), "A");
    Appointment appt2 = new Appointment(
        getDate("12/12/2020 12:52 pm"), getDate("4/5/2021 2:52 am"), "B");
    AppointmentBook book = new AppointmentBook("y#m&l");
    book.addAppointment(appt);
    book.addAppointment(appt1);
    book.addAppointment(appt2);
    StringWriter fw = new StringWriter();
    TextDumper td = new TextDumper(fw);
    td.dump(book);
    String exp = "y\\#m\\&l&12/12/2020 12:52 PM#4/5/2021 2:52 AM#A&12/12/2020 12:52 PM#4/5/2021 2:52 AM#B&12/12/2020 12:52 PM#4/5/2021 2:52 AM#C&";
    assertThat(exp, equalTo(fw.toString()));
  }

}
