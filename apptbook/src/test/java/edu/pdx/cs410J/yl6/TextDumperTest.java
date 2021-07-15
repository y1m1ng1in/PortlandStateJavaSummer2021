package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.Reader;
import java.text.ParseException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import edu.pdx.cs410J.ParserException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

/**
 * Unit tests for the {@link TextParser} class.
 */
public class TextDumperTest {

  static final String testFile = "unittest.txt";


  String readFile() throws IOException {
    Reader reader = new FileReader(testFile);
    StringBuilder sb = new StringBuilder();
    int c;
    while((c = reader.read()) != -1) {
      sb.append((char) c);
    }
    reader.close();
    return sb.toString();
  }

  @Test
  void writeOneAppt() throws IOException, ParseException {
    Appointment appt = new Appointment(
        "12/12/2019 12:52 am", "4/5/2020 2:52 pm", "A description");
    AppointmentBook book = new AppointmentBook("yml");
    book.addAppointment(appt);
    TextDumper<AppointmentBook, Appointment> td = new TextDumper<>(testFile);
    td.dump(book);
    String content = readFile();
    String exp = "yml&12/12/2019 12:52 am#4/5/2020 2:52 pm#A description&";
    assertThat(content, equalTo(exp));
  }

  @Test
  void writeThreeAppt() throws IOException, ParseException {
    Appointment appt = new Appointment(
        "12/12/2020 12:52 pm", "4/5/2021 2:52 am", "C");
    Appointment appt1 = new Appointment(
        "12/12/2020 12:52 pm", "4/5/2021 2:52 am", "A");
    Appointment appt2 = new Appointment(
        "12/12/2020 12:52 pm", "4/5/2021 2:52 am", "B");
    AppointmentBook book = new AppointmentBook("y#m&l");
    book.addAppointment(appt);
    book.addAppointment(appt1);
    book.addAppointment(appt2);
    TextDumper<AppointmentBook, Appointment> td = new TextDumper<>(testFile);
    td.dump(book);
    String content = readFile();
    String exp = "y\\#m\\&l&12/12/2020 12:52 pm#4/5/2021 2:52 am#A&12/12/2020 12:52 pm#4/5/2021 2:52 am#B&12/12/2020 12:52 pm#4/5/2021 2:52 am#C&";
    assertThat(content, equalTo(exp));
  }

}
