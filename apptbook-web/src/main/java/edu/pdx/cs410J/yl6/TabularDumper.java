package edu.pdx.cs410J.yl6;

import java.util.Arrays;

public abstract class TabularDumper<T> implements Dumper<T> {

  protected final int maxLine = 40;
  protected final int tableBoundaryPadding = 2;
  protected final String border = fillArray(maxLine, '-') + "\n";

  protected int fieldNameWidth = 13;

  /** 
   * Create a string that display the name of the field, which a vertical bar
   * that delimits the name of the field from the actual field content.
   * 
   * @param s the name of the field
   * @return  a string that represents the name of the field that is padded
   *          with extra spaces and a vertical bar appended at the end, which
   *          makes it to be aligned with rest of the name of the fields
   */
  protected String formatFieldName(String s) {
    return String.format("%-" + fieldNameWidth + "s|  ", s);
  }

  /**
   * Format a string that is too long, such that the resulting string contains
   * multiple lines and the length of each line is limited by <code>maxLine</code>
   * which is a fixed value preset by this class.
   * 
   * @param s the string to be formatted
   * @return the formatted string
   */
  protected String formatField(String s) {
    StringBuilder sb = new StringBuilder();
    int fieldWidth = maxLine - fieldNameWidth - 1 - tableBoundaryPadding;
    int currentLength = fieldWidth;
    String spacesForFieldName = fillArray(fieldNameWidth, ' ') + '|' + fillArray(tableBoundaryPadding, ' ');

    if (fieldWidth > s.length()) {
      return s;
    }

    sb.append(s.substring(0, fieldWidth));
    while (currentLength < s.length()) {
      sb.append('\n');
      sb.append(spacesForFieldName);
      if (currentLength + fieldWidth > s.length()) {
        sb.append(s.substring(currentLength, s.length()));
      } else {
        sb.append(s.substring(currentLength, currentLength + fieldWidth));
      }
      currentLength += fieldWidth;
    }

    return sb.toString();
  }

  /**
   * Construct a string that is formed by same character, <code>value</code>, with
   * length. <code>length</code>.
   * 
   * @param length the length of the String
   * @param value  the value of each element to be filled
   * @return a string that is formed with same character
   */
  protected String fillArray(int length, char value) {
    char[] array = new char[length];
    Arrays.fill(array, value);
    return new String(array);
  }

}
