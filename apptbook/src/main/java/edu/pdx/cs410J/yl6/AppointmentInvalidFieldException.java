package edu.pdx.cs410J.yl6;

@SuppressWarnings("serial")
public class AppointmentInvalidFieldException extends Exception {

  /**
   * Creates a new <code>AppointmentInvalidFieldException</code> with a given
   * descriptive message.
   */
  public AppointmentInvalidFieldException(String description) {
    super(description);
  }

  /**
  * Creates a new <code>AppointmentInvalidFieldException</code> that was caused by
  * another exception.
  */
  public AppointmentInvalidFieldException(String description, Throwable cause) {
    super(description, cause);
  }

}