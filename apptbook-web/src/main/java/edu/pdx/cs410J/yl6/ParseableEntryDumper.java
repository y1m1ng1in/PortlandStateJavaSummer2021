package edu.pdx.cs410J.yl6;

import java.io.Writer;
import java.io.IOException;

/**
 * ParseableEntryDumper is the class that provides a "template method" of
 * <code>dump</code> such that its derived classes can specify what specific
 * entries should be dumped via a {@link Writer}. The template method dumps each
 * field after adding escaped chars for confliction with delimiters by
 * {@link ParseableDumper#addEscapeCharacter}, the order of each field is same
 * as returned from <code>getStringFields</code> from low index to high index.
 * Each field is delimited by {@link ParseableDumper#fieldDelimiter}. After all
 * fields are dumped, a {@link ParseableDumper#entryDelimiter} is dumped at the
 * end.
 */
public abstract class ParseableEntryDumper<T> extends ParseableDumper<T> {

  protected Writer writer;

  /**
   * Create a instance of concrete class derived from this class
   * 
   * @param writer a {@link Writer} instance used to dump
   */
  public ParseableEntryDumper(Writer writer) {
    this.writer = writer;
  }

  /**
   * Template method for dumping instance of <code>T</code>
   * 
   * @param entry the object to be dumped
   * @throws IOException If an input or output exception occurs
   */
  public void dump(T entry) throws IOException {
    String[] appointmentFields = getStringFields(entry);

    for (int i = 0; i < appointmentFields.length; ++i) {
      this.writer.write(addEscapeCharacter(appointmentFields[i]));
      if (i + 1 == appointmentFields.length) {
        this.writer.write(this.entryDelimiter);
      } else {
        this.writer.write(this.fieldDelimiter);
      }
    }
  }

  /**
   * Return an array of fields to be dumped, the order of fields to be dumped is
   * same as they appear in the returned array from low index to high index.
   * 
   * @param entry the instance of <code>T</code> to be dumped
   * @return an array of fields
   */
  public abstract String[] getStringFields(T entry);
}
