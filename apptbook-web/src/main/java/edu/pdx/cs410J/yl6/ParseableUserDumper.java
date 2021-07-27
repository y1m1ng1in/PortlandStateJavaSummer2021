package edu.pdx.cs410J.yl6;

import java.io.Writer;

/**
 * ParseableUserDumper is the class that dumps the information of an
 * {@link User} via a {@link Writer} specified by constructor.
 */
public class ParseableUserDumper extends ParseableEntryDumper<User> {

  protected Writer writer;

  /**
   * Create a ParseableUserDumper instance
   * 
   * @param writer the {@link Writer} instance to be used in
   *               {@link ParseableUserDumper#dump}
   */
  public ParseableUserDumper(Writer writer) {
    super(writer);
  }

  /**
   * Return an array of strings in the order of each field of an user to be
   * dumped.
   * 
   * @param appointment the {@link User} instance to be dumped
   * @return an array of strings in the order of each field of an user to be
   *         dumped
   */
  public String[] getStringFields(User user) {
    return new String[] { user.getId().toString(), user.getUsername(), user.getPassword(), user.getEmail(),
        user.getAddress() };
  }
}
