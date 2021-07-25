package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;
import java.io.IOException;
import java.text.ParseException;

import edu.pdx.cs410J.ParserException;

public class TextAppointmentBookParserTest {

  @Test
  void testCase1() throws ParserException, IOException, ParseException {
    String s = "owner name&x";
    StringReader reader = new StringReader(s);
    TextAppointmentBookParser p = new TextAppointmentBookParser(reader);
    AppointmentBook<Appointment> b = p.parse();
    assertThat(b.getOwnerName(), equalTo("owner name"));
    assertThat(reader.read(), equalTo((int) 'x'));
  }

  @Test
  void testCase2() throws ParserException, IOException, ParseException {
    String s = "owner#name&x";
    StringReader reader = new StringReader(s);
    TextAppointmentBookParser p = new TextAppointmentBookParser(reader);
    Exception ex = assertThrows(ParserException.class, p::parse);
    assertThat(ex.getMessage(), containsString("Prohibited character # occurs when parsing owner name"));
  }

  @Test
  void testCase3() throws ParserException, IOException, ParseException {
    String s = "owner namex";
    StringReader reader = new StringReader(s);
    TextAppointmentBookParser p = new TextAppointmentBookParser(reader);
    Exception ex = assertThrows(ParserException.class, p::parse);
    assertThat(ex.getMessage(), containsString("End of file reached before the field been parsed completely"));
  }

  @Test
  void testCase4() throws ParserException, IOException, ParseException {
    String s = "owner\\\\name&x";
    StringReader reader = new StringReader(s);
    TextAppointmentBookParser p = new TextAppointmentBookParser(reader);
    AppointmentBook<Appointment> b = p.parse();
    assertThat(b.getOwnerName(), equalTo("owner\\name"));
    assertThat(reader.read(), equalTo((int) 'x'));
  }

  @Test
  void testCase5() throws ParserException, IOException, ParseException {
    String s = "   &x";
    StringReader reader = new StringReader(s);
    TextAppointmentBookParser p = new TextAppointmentBookParser(reader);
    Exception ex = assertThrows(ParserException.class, p::parse);
    assertThat(ex.getMessage(), containsString("Field owner should not be empty"));
  }
}
