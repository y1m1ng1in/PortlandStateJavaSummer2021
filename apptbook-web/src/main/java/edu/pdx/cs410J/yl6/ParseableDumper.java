package edu.pdx.cs410J.yl6;

/**
 * The abstract class that encapsulates common methods needed for dumping an
 * instance of <code>T</code> such that <code>T</code> can be instantiated by
 * reading exactly what has been dumped. The "common methods" supported in this
 * class include such as adding escaped character before delimiter of fields and
 * entries.
 */
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
