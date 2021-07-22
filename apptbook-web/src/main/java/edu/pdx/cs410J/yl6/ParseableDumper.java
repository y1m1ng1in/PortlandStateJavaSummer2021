package edu.pdx.cs410J.yl6;

public abstract class ParseableDumper<T> implements Dumper<T> {

  protected char fieldDelimiter = '#';
  protected char entryDelimiter = '&';
  protected char escapeCharacter = '\\';

  /**
   * Scan <code>s</code> and add '\' before any character that conflicts with
   * delimiters and '\'
   * 
   * @param s the string to be scanned and add '\' before any character that
   *          conflicts with delimiters and '\'.
   * @return a string that has been processed as above.
   */
  public String addEscapeCharacter(String s) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < s.length(); ++i) {
      char c = s.charAt(i);
      if (c == fieldDelimiter || c == entryDelimiter || c == escapeCharacter) {
        sb.append(escapeCharacter);
      }
      sb.append(c);
    }
    return sb.toString();
  }

}
