package edu.pdx.cs410J.yl6.database.plaintextoperator;

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.yl6.Appointment;
import edu.pdx.cs410J.yl6.AppointmentSlot;
import edu.pdx.cs410J.yl6.Helper;

import java.io.Reader;
import java.util.Date;

public abstract class AppointmentTableEntryParser {

    public static class AppointmentDescriptionTableEntryParser extends TableEntryParser<Appointment> {

        public static final String ATTEMPT_TO_INSTANTIATE_APPOINTMENT = "cannot instantiate an Appointment instance " +
                "via description table";
        private static final int numberOfField = 2;

        public AppointmentDescriptionTableEntryParser(Reader reader) {
            this.reader = reader;
        }

        @Override
        public int getExpectedNumberOfField() {
            return numberOfField;
        }

        @Override
        public Appointment instantiate(String... fields) throws ParserException {
            throw new IllegalCallerException(ATTEMPT_TO_INSTANTIATE_APPOINTMENT);
        }

    }

    public static class AppointmentSlotTableEntryParser extends TableEntryParser<AppointmentSlot> {

        private static final int numberOfField = 3;

        public AppointmentSlotTableEntryParser(Reader reader) {
            this.reader = reader;
        }

        @Override
        public int getExpectedNumberOfField() {
            return numberOfField;
        }

        @Override
        public AppointmentSlot instantiate(String... fields) throws ParserException {
            Date begin = Helper.validateAndParseDate(fields[0]);
            Date end = Helper.validateAndParseDate(fields[1]);
            if (!Helper.validateAndGetDateInterval(begin, end, "begin time of appointment slot",
                    "end time of appointment slot")) {
                throw new ParserException(Helper.getErrorMessage());
            }
            return new AppointmentSlot(fields[2], begin, end);
        }
    }
}
