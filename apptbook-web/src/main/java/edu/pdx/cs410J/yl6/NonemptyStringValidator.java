package edu.pdx.cs410J.yl6;

/** 
 * NonemptyStringValidator is the class that encapsulate the computation of validation
 * of a given string <code>s</code> that is not empty after removing leading and tailing
 * spaces.   
 */
public class NonemptyStringValidator extends AbstractValidator {
  
  private String message;
  private String fieldName;

  /**
   * Create a NonemptyStringValidator instance.
   * 
   * @param fieldName the name of the field to be displayed in the error message
   *                  indicates validation violation.
   */
  public NonemptyStringValidator(String fieldName) {
    this.fieldName = fieldName;
  }

  /**
   * Given a string <code>s</code>, check if it is empty after removing leading 
   * and tailing spaces. If it is empty, exit the program with status 1 with error 
   * message indicates that field <code>fieldName</code> is empty.  
   * 
   * @param s the string to check whether it is empty after being trimed.
   * @return  <code>true</code> if the string <code>s</code> is not empty after trimed;
   *          <code>false</code> otherwise. 
   */
  @Override
  public boolean isValid(String s) {
    String trimed = s.trim();
    
    if (trimed.equals("")) {
      this.message = "Field " + this.fieldName + " should not be empty";
      return false;
    }
    return true;
  }

  /**
   * Return error message generated by last <code>isValid</code> call.
   * 
   * @return error message computed during last <code>isValid</code> call.
   */
  @Override
  public String getErrorMessage() {
    return this.message;
  }
}