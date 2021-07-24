package edu.pdx.cs410J.yl6;

import java.io.Writer;

public class ParseableUserDumper extends ParseableEntryDumper<User> {

  protected Writer writer;

  public ParseableUserDumper(Writer writer) {
    super(writer);
  }

  public String[] getStringFields(User user) {
    return new String[] { user.getId().toString(), user.getUsername(), user.getPassword(), user.getEmail(),
        user.getAddress() };
  }
}
