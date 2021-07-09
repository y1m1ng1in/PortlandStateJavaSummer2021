package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link DateStringValidator} class.
 */
public class DateStringValidatorTest {

  @Test
  void validStringCase1() {
    DateStringValidator v = new DateStringValidator();
    assertThat(v.isValid("3/14/2020"), equalTo(true));
  }

  @Test
  void validStringCase2() {
    DateStringValidator v = new DateStringValidator();
    assertThat(v.isValid("12/14/2020"), equalTo(true));
  }

  @Test
  void validStringCase3() {
    DateStringValidator v = new DateStringValidator();
    assertThat(v.isValid("12/01/2020"), equalTo(true));
  }

  @Test
  void validStringCase4() {
    DateStringValidator v = new DateStringValidator();
    assertThat(v.isValid("02/01/2020"), equalTo(true));
  }

  @Test
  void validStringCase5() {
    DateStringValidator v = new DateStringValidator();
    assertThat(v.isValid("2/1/2020"), equalTo(true));
  }
  
  @Test
  void outofrangeCase1() {
    DateStringValidator v = new DateStringValidator();
    assertThat(v.isValid("21/1/2020"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("is not a valid month"));
  }

  @Test
  void outofrangeCase2() {
    DateStringValidator v = new DateStringValidator();
    assertThat(v.isValid("1/32/2020"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("is not a valid day"));
  }

  @Test
  void outofrangeCase3() {
    DateStringValidator v = new DateStringValidator();
    assertThat(v.isValid("13/01/2020"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("is not a valid month"));
  }

  @Test
  void incorrectFormatCase1() {
    DateStringValidator v = new DateStringValidator();
    assertThat(v.isValid("12-01-2020"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("format does not meet requirement"));
  }

  @Test
  void incorrectFormatCase2() {
    DateStringValidator v = new DateStringValidator();
    assertThat(v.isValid("12012020"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("format does not meet requirement"));
  }

  @Test
  void incorrectFormatCase3() {
    DateStringValidator v = new DateStringValidator();
    assertThat(v.isValid("thisisadate"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("format does not meet requirement"));
  }

  @Test
  void incorrectFormatCase4() {
    DateStringValidator v = new DateStringValidator();
    assertThat(v.isValid("12/01/20"), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("format does not meet requirement"));
  }

  
}
