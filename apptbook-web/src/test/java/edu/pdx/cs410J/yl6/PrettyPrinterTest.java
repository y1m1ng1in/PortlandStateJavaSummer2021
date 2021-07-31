package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.ParseException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PrettyPrinterTest {
  
  String prettyFile = "prettyfile.txt";

  Date getDate(String s) throws ParseException {
    DateFormat df = new SimpleDateFormat("M/d/yyyy h:m a");
    df.setLenient(false);
    return df.parse(s);
  }

  @Test
  void testCase1() throws ParseException, IOException {
    StringWriter sw = new StringWriter();
    PrettyPrinter printer = new PrettyPrinter(sw);
    AppointmentBook book = new AppointmentBook("a owner");
    Appointment appointment1 = new Appointment(getDate("3/14/2020 4:29 pm"),getDate("3/14/2020 4:50 pm"),"dummy1");
    Appointment appointment2 = new Appointment(getDate("3/16/2020 4:29 pm"),getDate("3/16/2020 4:50 pm"),"dummy2");
    book.addAppointment(appointment1);
    book.addAppointment(appointment2);
    printer.dump(book);
    String s = sw.toString();
    String exp = 
        "Owner        |  a owner\n" +
        "----------------------------------------\n" +
        "Begin at     |  3/14/20, 4:29 PM\n" + 
        "End at       |  3/14/20, 4:50 PM\n" +
        "Description  |  dummy1\n" +
        "Duration     |  21 minutes\n" + 
        "----------------------------------------\n" +
        "Begin at     |  3/16/20, 4:29 PM\n" + 
        "End at       |  3/16/20, 4:50 PM\n" +
        "Description  |  dummy2\n" +
        "Duration     |  21 minutes\n" +
        "----------------------------------------\n";
    assertThat(s, equalTo(exp));
  }

  @Test
  void testCase2() throws ParseException, IOException {
    StringWriter sw = new StringWriter();
    PrettyPrinter printer = new PrettyPrinter(sw);
    AppointmentBook book = new AppointmentBook("a owner");
    Appointment appointment1 = new Appointment(getDate("3/14/2020 4:29 pm"),getDate("3/14/2020 4:50 pm"),"dummy1");
    Appointment appointment2 = new Appointment(getDate("3/16/2020 4:29 pm"),getDate("3/16/2020 4:50 pm"),"dummy2");
    Appointment appointment3 = new Appointment(getDate("1/12/2020 5:30 pm"),getDate("1/12/2020 6:50 pm"),
        "supersupersuper supersupersuper supersupersuper longlonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglonglong description");
    book.addAppointment(appointment1);
    book.addAppointment(appointment2);
    book.addAppointment(appointment3);
    printer.dump(book);
    String s = sw.toString();
    String exp = 
        "Owner        |  a owner\n" +
        "----------------------------------------\n" +
        "Begin at     |  1/12/20, 5:30 PM\n" + 
        "End at       |  1/12/20, 6:50 PM\n" +
        "Description  |  supersupersuper supersup\n"+
        "             |  ersuper supersupersuper \n" + 
        "             |  longlonglonglonglonglong\n" + 
        "             |  longlonglonglonglonglong\n" + 
        "             |  longlonglonglonglonglong\n" + 
        "             |   description\n" +
        "Duration     |  80 minutes\n" + 
        "----------------------------------------\n" +
        "Begin at     |  3/14/20, 4:29 PM\n" + 
        "End at       |  3/14/20, 4:50 PM\n" +
        "Description  |  dummy1\n" +
        "Duration     |  21 minutes\n" + 
        "----------------------------------------\n" +
        "Begin at     |  3/16/20, 4:29 PM\n" + 
        "End at       |  3/16/20, 4:50 PM\n" +
        "Description  |  dummy2\n" +
        "Duration     |  21 minutes\n" +
        "----------------------------------------\n";
    assertThat(s, equalTo(exp));
  }

}