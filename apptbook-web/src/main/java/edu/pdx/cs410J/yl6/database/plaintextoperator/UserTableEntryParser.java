package edu.pdx.cs410J.yl6.database.plaintextoperator;

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.yl6.User;

import java.io.Reader;

public abstract class UserTableEntryParser {

    public static class UserProfilerTableEntryParser extends TableEntryParser<User> {

        private static final int numberOfField = 5;

        public UserProfilerTableEntryParser(Reader reader) {
            this.reader = reader;
        }

        @Override
        public int getExpectedNumberOfField() {
            return numberOfField;
        }

        @Override
        public User instantiate(String... fields) throws ParserException {
            return new User(fields[0], fields[1], fields[2], fields[3], fields[4]);
        }
    }
}
