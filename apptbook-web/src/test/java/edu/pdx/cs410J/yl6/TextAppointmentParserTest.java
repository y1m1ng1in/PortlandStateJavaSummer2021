package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;
import java.io.IOException;
import java.text.ParseException;

import edu.pdx.cs410J.ParserException;

public class TextAppointmentParserTest {
  
  AppointmentValidator validator = new AppointmentValidator("M/d/yyyy h:m a");

  @Test
  void testCase1() throws ParserException, IOException, ParseException {
    String s = "2/2/2020 4:30 pm#2/2/2020 5:30 pm#description&";
    StringReader reader = new StringReader(s);
    TextAppointmentParser p = new TextAppointmentParser(reader, validator);
    Appointment appt = p.parse();
    Appointment expected = new Appointment("2/2/2020 4:30 pm", "2/2/2020 5:30 pm", "description");
    assertThat(appt.compareTo(expected), equalTo(0));
  }

  @Test
  void testCase2() throws ParserException, IOException, ParseException {
    String s = "2/2/2020 4:30 pm#2/2/2020 5:30 pm#description&x";
    StringReader reader = new StringReader(s);
    TextAppointmentParser p = new TextAppointmentParser(reader, validator);
    Appointment appt = p.parse();
    Appointment expected = new Appointment("2/2/2020 4:30 pm", "2/2/2020 5:30 pm", "description");
    assertThat(appt.compareTo(expected), equalTo(0));
    assertThat(reader.read(), equalTo((int) 'x'));
  }

  @Test
  void testCase3() throws ParserException, IOException, ParseException {
    String s = "2/2/2020 4:30 pm 2/2/2020 5:30 pm#description&x";
    StringReader reader = new StringReader(s);
    TextAppointmentParser p = new TextAppointmentParser(reader, validator);
    Exception ex = assertThrows(ParserException.class, p::parse);
    assertThat(ex.getMessage(), containsString("Not enough fields to build appointment from file"));
  }

  @Test
  void testCase4() throws ParserException, IOException, ParseException {
    String s = "2/2/2020 4:30 pm#2/2/2020#5:30 pm#description&x";
    StringReader reader = new StringReader(s);
    TextAppointmentParser p = new TextAppointmentParser(reader, validator);
    Exception ex = assertThrows(ParserException.class, p::parse);
    assertThat(ex.getMessage(), containsString("An extraneous field encountered to build appointment from file"));
  }

  @Test
  void testCase5() throws ParserException, IOException, ParseException {
    String s = "";
    StringReader reader = new StringReader(s);
    TextAppointmentParser p = new TextAppointmentParser(reader, validator);
    Appointment appt = p.parse();
    boolean b = appt == null;
    assertThat(b, equalTo(true));
  }

  @Test
  void testCase6() throws ParserException, IOException, ParseException {
    String s = "2/2/2020 4:30 pm#2/2/XXXX 5:30 pm#description&";
    StringReader reader = new StringReader(s);
    TextAppointmentParser p = new TextAppointmentParser(reader, validator);
    Exception ex = assertThrows(ParserException.class, p::parse);
    assertThat(ex.getMessage(), containsString("Unparseable date: \"2/2/XXXX 5:30 pm\""));
  }

  @Test
  void testCase7() throws ParserException, IOException, ParseException {
    String s = "2/2/2020 4:30 pm#2/2/2020 5:30 pm#des\\\\cr\\#iption&";
    StringReader reader = new StringReader(s);
    TextAppointmentParser p = new TextAppointmentParser(reader, validator);
    Appointment appt = p.parse();
    Appointment expected = new Appointment("2/2/2020 4:30 pm", "2/2/2020 5:30 pm", "des\\cr#iption");
    assertThat(appt.compareTo(expected), equalTo(0));
  }

  @Test
  void testCase8() throws ParserException, IOException, ParseException {
    String s = "2/2/2020 4:30 pm#2/2/2020 5:30 pm#description\\";
    StringReader reader = new StringReader(s);
    TextAppointmentParser p = new TextAppointmentParser(reader, validator);
    Exception ex = assertThrows(ParserException.class, p::parse);
    assertThat(ex.getMessage(), containsString("End of file reached before the field been parsed completely"));
  }
  
}
