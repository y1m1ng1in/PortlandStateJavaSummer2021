package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link DateStringValidator} class.
 */
public class TimeStringValidatorTest {

  @Test
  void validStringCase1() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("04:02"), equalTo(true));
  }

  @Test
  void validStringCase2() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("04:12"), equalTo(true));
  }

  @Test
  void validStringCase3() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("00:00"), equalTo(false));
  }

  @Test
  void validStringCase4() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("0:0"), equalTo(false));
  }

  @Test
  void validStringCase5() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("00:0"), equalTo(false));
  }

  @Test
  void validStringCase6() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("0:00"), equalTo(false));
  }

  @Test
  void validStringCase7() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("23:59"), equalTo(false));
  }

  @Test
  void outofrangeCase1() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("24:00"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("is not a valid hour"));
  }

  @Test
  void outofrangeCase2() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("23:60"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("is not a valid hour"));
  }

  @Test
  void outofrangeCase3() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("23:80"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("is not a valid hour"));
  }

  @Test
  void incorrectFormatCase1() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("2359"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("format does not meet requirement"));
  }

  @Test
  void incorrectFormatCase2() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("23:590"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("format does not meet requirement"));
  }

  @Test
  void incorrectFormatCase3() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("230:59"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("format does not meet requirement"));
  }

  @Test
  void incorrectFormatCase4() {
    TimeStringValidator v = new TimeStringValidator();
    assertThat(v.isValid("thisisatime"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("format does not meet requirement"));
  }
}
