package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppointmentValidatorTest {

  @Test
  void validFieldsTest() {
    AppointmentValidator v = new AppointmentValidator("M/d/yyyy h:m a");
    String[] fs = { "3/14/2020 4:29 pm", "3/14/2020 4:50 pm", "dummy" };
    assertThat(v.isValid(fs), equalTo(true));
  }

  @Test
  void BeginAfterEnd() {
    AppointmentValidator v = new AppointmentValidator("M/d/yyyy h:m a");
    String[] fs = { "3/14/2020 4:50 pm", "3/14/2020 4:29 pm", "dummy" };
    assertThat(v.isValid(fs), equalTo(false));
    assertThat(v.getErrorMessage(), containsString(
        "Begin time is not early than end time of appointment, begin at "));
  }

  @Test
  void BeginEqualEnd() {
    AppointmentValidator v = new AppointmentValidator("M/d/yyyy h:m a");
    String[] fs = { "3/14/2020 4:29 pm", "3/14/2020 4:29 pm", "dummy" };
    assertThat(v.isValid(fs), equalTo(false));
    assertThat(v.getErrorMessage(), containsString(
        "Begin time is not early than end time of appointment, begin at "));
  }

  @Test
  void emptyDescriptionCase1() {
    AppointmentValidator v = new AppointmentValidator("M/d/yyyy h:m a");
    String[] fs = { "3/14/2020 4:29 pm", "3/14/2020 4:29 pm", "" };
    assertThat(v.isValid(fs), equalTo(false));
    assertThat(v.getErrorMessage(), containsString(
        "Field description should not be empty"));
  }

  @Test
  void emptyDescriptionCase2() {
    AppointmentValidator v = new AppointmentValidator("M/d/yyyy h:m a");
    String[] fs = { "3/14/2020 4:29 pm", "3/14/2020 4:29 pm", "    " };
    assertThat(v.isValid(fs), equalTo(false));
    assertThat(v.getErrorMessage(), containsString(
        "Field description should not be empty"));
  }

  @Test
  void unparseableBegin() {
    AppointmentValidator v = new AppointmentValidator("M/d/yyyy h:m a");
    String[] fs = { "3-14-2020 4:29 pm", "3/14/2020 4:29 pm", "dummy" };
    assertThat(v.isValid(fs), equalTo(false));
    assertThat(v.getErrorMessage(), containsString(
        "Unparseable date: \"3-14-2020 4:29 pm\""));
  }

  @Test
  void unparseableEnd() {
    AppointmentValidator v = new AppointmentValidator("M/d/yyyy h:m a");
    String[] fs = { "3/14/2020 4:29 pm", "3 14 2020 4:29 pm", "dummy" };
    assertThat(v.isValid(fs), equalTo(false));
    assertThat(v.getErrorMessage(), containsString(
        "Unparseable date: \"3 14 2020 4:29 pm\""));
  }

  @Test
  void unparseableEndCase2() {
    AppointmentValidator v = new AppointmentValidator("M/d/yyyy h:m a");
    String[] fs = { "3/14/2020 4:29 pm", "3/14/2020 4:29", "dummy" };
    assertThat(v.isValid(fs), equalTo(false));
    assertThat(v.getErrorMessage(), containsString(
        "Unparseable date: \"3/14/2020 4:29\""));
  }


}