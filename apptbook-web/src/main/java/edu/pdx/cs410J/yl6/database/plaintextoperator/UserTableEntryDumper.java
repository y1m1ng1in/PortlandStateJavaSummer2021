package edu.pdx.cs410J.yl6.database.plaintextoperator;

import edu.pdx.cs410J.yl6.User;

import java.io.Writer;

public abstract class UserTableEntryDumper {

    public static class UserProfilerTableEntryDumper extends TableEntryDumper<User> {

        public UserProfilerTableEntryDumper(Writer writer) {
            super(writer);
        }

        @Override
        public String[] getStringFields(User user) {
            return new String[]{
                    user.getId().toString(), user.getUsername(), user.getPassword(), user.getEmail(), user.getAddress()
            };
        }
    }


}
