package edu.pdx.cs410J.yl6;

import java.io.Reader;

import edu.pdx.cs410J.ParserException;

public class TextUserParser extends Parser<User> {
  
  final private int numberofField = 5;

  public TextUserParser(Reader reader) {
    this.reader = reader;
  }

  @Override
  public int getExpectedNumberofField() {
    return numberofField;
  }

  @Override
  public User instantiate(String... fields) throws ParserException {
    return new User(fields[0], fields[1], fields[2], fields[3], fields[4]);
  }
}
