package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;
import java.io.IOException;
import java.text.ParseException;

import edu.pdx.cs410J.ParserException;

public class TextParserTest {

  AppointmentValidator createValidators() {
    return new AppointmentValidator("M/d/yyyy h:m a");
  }

  /**
   * Tests that correct begin date and time is returned as a string.
   */
  @Test
  void wellFormattedWithOneAppt() throws IOException, ParserException {
    String s = "yml&2/2/2020 2:22 am#2/3/2020 3:33 am#descrp&";
    StringReader reader = new StringReader(s);
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser apptParser = new TextAppointmentParser(reader, createValidators());
    TextParser textParser = new TextParser(new TextAppointmentBookParser(reader), new TextAppointmentParser(reader, createValidators()));
    AppointmentBook<Appointment> book = textParser.parse();  
    Appointment inBook = book.getAppointments().first();
    
    String s1 = inBook.getBeginTimeString();
    assertThat(s1, equalTo("2/2/2020 2:22 AM"));
    s1 = inBook.getEndTimeString();
    assertThat(s1, equalTo("2/3/2020 3:33 AM"));
    s1 = inBook.getDescription();
    assertThat(s1, equalTo("descrp"));
  }

  @Test
  void wellFormattedWithTwoAppts() throws IOException, ParserException {
    String s =
        "yml&2/2/2020 2:22 pm#2/3/2020 3:33 pm#descrp&11/14/2021 12:30 am#11/15/2021 2:45 pm#desc  des ddd &";
    StringReader reader = new StringReader(s);
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser apptParser = new TextAppointmentParser(reader, createValidators());
    TextParser textParser = new TextParser(bookParser, apptParser);
    AppointmentBook<Appointment> book = textParser.parse(); 
    Appointment inBook1 = book.getAppointments().pollFirst();
    Appointment inBook2 = book.getAppointments().pollFirst();

    String s1 = inBook1.getBeginTimeString();
    assertThat(s1, equalTo("2/2/2020 2:22 PM"));
    s1 = inBook1.getEndTimeString();
    assertThat(s1, equalTo("2/3/2020 3:33 PM"));
    s1 = inBook1.getDescription();
    assertThat(s1, equalTo("descrp"));

    s1 = inBook2.getBeginTimeString();
    assertThat(s1, equalTo("11/14/2021 12:30 AM"));
    s1 = inBook2.getEndTimeString();
    assertThat(s1, equalTo("11/15/2021 2:45 PM"));
    s1 = inBook2.getDescription();
    assertThat(s1, equalTo("desc  des ddd "));
  }

  @Test
  void wellFormattedWithEscapedCharWithOneAppt() throws IOException, ParserException {
    String f = 
        "\\#\\#\\#\\&\\&\\&\\#\\#\\#&2/2/2020 2:22 pm#2/3/2020 3:33 pm#\\&\\&des\\&c\\&\\&\\&\\&&";
    StringReader reader = new StringReader(f);
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser apptParser = new TextAppointmentParser(reader, createValidators());
    TextParser textParser = new TextParser(bookParser, apptParser);
    AppointmentBook<Appointment> book = textParser.parse(); 
    Appointment inBook1 = book.getAppointments().first();

    String s = inBook1.getBeginTimeString();
    assertThat(s, equalTo("2/2/2020 2:22 PM"));
    s = inBook1.getEndTimeString();
    assertThat(s, equalTo("2/3/2020 3:33 PM"));
    s = inBook1.getDescription();
    assertThat(s, equalTo("&&des&c&&&&"));
  }

  @Test
  void missingAmpersandInTheEnd() throws IOException, ParserException {
    String f = 
        "\\#\\#\\#\\\\\\\\\\#\\#\\#&2/2/2020 2:22 pm#2/3/2020 3:33 pm#\\&\\&des\\&c\\&\\&\\&\\";
    StringReader reader = new StringReader(f);
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser apptParser = new TextAppointmentParser(reader, createValidators());
    TextParser textParser = new TextParser(bookParser, apptParser);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("End of file reached before the field been parsed completely"));
  }

  @Test
  void missingArgumentToBuildAppt() throws IOException, ParserException {
    String f = 
        "\\#\\#\\#\\\\\\\\\\#\\#\\#&2/2/2020 2:22 2/3/2020 3:33#\\&\\&des\\&c\\&\\&\\&\\&&";
    StringReader reader = new StringReader(f);
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser apptParser = new TextAppointmentParser(reader, createValidators());
    TextParser textParser = new TextParser(bookParser, apptParser);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("Not enough fields to build appointment from file expect 3, but got 2"));
  }

  @Test
  void missingOwner() throws IOException, ParserException {
    String f =
        "2/2/2020 2:22 2/3/2020 3:33#\\&\\&des\\&c\\&\\&\\&\\&&";
    StringReader reader = new StringReader(f);
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser apptParser = new TextAppointmentParser(reader, createValidators());
    TextParser textParser = new TextParser(bookParser, apptParser);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("Prohibited character # occurs when parsing owner name."));
  }

  @Test
  void apptMissingEverything() throws IOException, ParserException {
    String f = "owner&&";
    StringReader reader = new StringReader(f);
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser apptParser = new TextAppointmentParser(reader, createValidators());
    TextParser textParser = new TextParser(bookParser, apptParser);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("Not enough fields to build appointment from file expect 3, but got 1"));
  }

  @Test
  void onlyOwner() throws IOException, ParserException {
    String f = "owner&";
    StringReader reader = new StringReader(f);
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser apptParser = new TextAppointmentParser(reader, createValidators());
    TextParser textParser = new TextParser(bookParser, apptParser);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("No appointment associated with owner owner"));
  }

  @Test
  void untestedCase1() throws IOException, ParserException {
    String f =
      "yml&2/2/2020 3:40 am#2/4/2020 04:3 am#test file with dir&" +
      "1/09/2020 3:40 am#2/04/2020 04:3 am#" +
      "test accessing file with dir02/09/2020 3:40 pm#2/04/2020 04:3 pm#test accessing file with dir&";
    StringReader reader = new StringReader(f);
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser apptParser = new TextAppointmentParser(reader, createValidators());
    TextParser textParser = new TextParser(bookParser, apptParser);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        equalTo("An extraneous field encountered to build appointment from file"));
  }

  @Test
  void validatorViolationCase1() throws IOException, ParserException {
    String f =
        "yml&22/2/2020 2:22 pm#2/3/2020 3:33 pm#descrp&";
    StringReader reader = new StringReader(f);
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser apptParser = new TextAppointmentParser(reader, createValidators());
    TextParser textParser = new TextParser(bookParser, apptParser);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        containsString("Unparseable date"));
  }

  @Test
  void validatorViolationCase2() throws IOException, ParserException {
    String f =
        "yml&02/02/2020 02:60 am#2/3/2020 3:33 am#descrp&";
    StringReader reader = new StringReader(f);
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser apptParser = new TextAppointmentParser(reader, createValidators());
    TextParser textParser = new TextParser(bookParser, apptParser);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        containsString("Unparseable date"));
  }

  @Test
  void testIOexception() throws IOException, ParserException {
    String f =
        "yml&02/02/2020 02:60 am#2/3/2020 3:33 am#descrp&";
    StringReader reader = new StringReader(f);
    reader.close();
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser apptParser = new TextAppointmentParser(reader, createValidators());
    TextParser textParser = new TextParser(bookParser, apptParser);
    Exception exception = assertThrows(ParserException.class, textParser::parse);
    assertThat(exception.getMessage(), 
        containsString("IOException occurs during parsing with message"));
  }

}
