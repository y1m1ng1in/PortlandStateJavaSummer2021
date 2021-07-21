package edu.pdx.cs410J.yl6;

/**
 * The exception occurred during communicating with {@link AppointmentBookStorage}
 */
public class StorageException extends Exception {
  
  /**
   * Create a StorageException instance
   * 
   * @param message the specific error message
   */
  public StorageException(String message) {
    super(message);
  }
}
