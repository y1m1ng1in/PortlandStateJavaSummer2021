package edu.pdx.cs410J.yl6;

/**
 * This interface is to be implemented by classes that can be written
 * to a plain text file and be parsed to create instances of those classes
 * from a plain text file. 
 * <p>
 * This interface contains two methods, where <code>getStringFields</code>
 * returns an array of strings which can be written to text file, it is 
 * expected for the implemented classes to be able to takes those strings as 
 * arguments to create instance of those implemented classes; 
 * <code>getExpectedNumberOfField</code> returns the expected number of fields
 * that should be parsed from text file in order to create an instance of an
 * implemented class. 
 */
public interface PlainTextRepresentable {

  /**
   * Returns an array of strings that are ready to be written to file.
   * (Depends on how the implemented class write to file and parse the file,
   * it may need to do some extra processing after an array of string returned,
   * for example, adding escape character '\' before each character of each string
   * to avoid confliction with delimiters). 
   * 
   * @return an array of strings
   */
  public String[] getStringFields();
  
  /**
   * Return the expected number of fields to be parsed from text file for each
   * class instance. 
   * 
   * @return an integer that expresses the number of fields expected to be parsed.
   */
  public int getExpectedNumberOfField(); 
}