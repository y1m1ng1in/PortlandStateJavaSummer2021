package edu.pdx.cs410J.yl6;

import java.io.Writer;
import java.io.IOException;

public abstract class ParseableEntryDumper<T> extends ParseableDumper<T> {
  
  protected Writer writer;

  public ParseableEntryDumper(Writer writer) {
    this.writer = writer;
  }

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

  public abstract String[] getStringFields(T entry);
}
