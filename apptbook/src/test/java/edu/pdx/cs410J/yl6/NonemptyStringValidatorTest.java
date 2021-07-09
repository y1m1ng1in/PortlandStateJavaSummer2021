package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link NonemptyStringValidator} class.
 */
public class NonemptyStringValidatorTest {

  @Test
  void stringWithNoSpace() {
    NonemptyStringValidator v = new NonemptyStringValidator("testField");
    assertThat(v.isValid("thisisastring"), equalTo(true));
  }

  @Test
  void stringWithNoSpaceAfterTrim() {
    NonemptyStringValidator v = new NonemptyStringValidator("testField");
    assertThat(v.isValid("  thisisastring  "), equalTo(true));
  }

  @Test
  void stringWithSpaceAfterTrim() {
    NonemptyStringValidator v = new NonemptyStringValidator("testField");
    assertThat(v.isValid("  t h i s i  s   astring  "), equalTo(true));
  }

  @Test
  void emptyAfterTrim() {
    NonemptyStringValidator v = new NonemptyStringValidator("testField");
    assertThat(v.isValid("    "), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("should not be empty"));
  }

  @Test
  void empty() {
    NonemptyStringValidator v = new NonemptyStringValidator("testField");
    assertThat(v.isValid(""), equalTo(false));
    assertThat(v.getErrorMessage(), containsString("should not be empty"));
  }
  
}
