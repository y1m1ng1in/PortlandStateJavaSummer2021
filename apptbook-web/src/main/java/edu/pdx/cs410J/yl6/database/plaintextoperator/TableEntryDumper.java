package edu.pdx.cs410J.yl6.database.plaintextoperator;

import java.io.IOException;
import java.io.Writer;

/**
 * Dumper is the interface that classes implement when functionality of dumper
 * an instance of type <code>T</code> is required. The instance of
 * <code>T</code> is dumped via some {@link java.io.Writer}.
 */
public abstract class TableEntryDumper<T> {

    protected char fieldDelimiter = '#';
    protected char entryDelimiter = '&';
    protected char escapeCharacter = '\\';

    protected Writer writer;

    /**
     * Create a instance of concrete class derived from this class
     *
     * @param writer a {@link Writer} instance used to dump
     */
    public TableEntryDumper(Writer writer) {
        this.writer = writer;
    }

    /**
     * Return an array of fields to be dumped, the order of fields to be dumped is
     * same as they appear in the returned array from low index to high index.
     *
     * @param entry the instance of <code>T</code> to be dumped
     * @return an array of fields
     */
    public abstract String[] getStringFields(T entry);

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
     * Scan <code>s</code> and add '\' before any character that conflicts with
     * delimiters and '\'
     *
     * @param s the string to be scanned and add '\' before any character that
     *          conflicts with delimiters and '\'.
     * @return a string that has been processed as above.
     */
    private String addEscapeCharacter(String s) {
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
