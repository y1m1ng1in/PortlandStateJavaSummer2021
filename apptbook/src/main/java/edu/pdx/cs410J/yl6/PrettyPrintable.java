package edu.pdx.cs410J.yl6;

/**
 * This interface is to be implemented by classes that can be dumped as a 
 * well-formatted textual presentation.
 */
public interface PrettyPrintable {

  /**
   * Get an array of strings that is to be dumped, and the order of each 
   * field is same as the returned array from low index to high index.
   * 
   * @return an array of strings that is to be dumped
   */
  public String[] getPrettyPrinterFields();
}