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

        private static final int numberOfField = 7;

        public AppointmentSlotTableEntryParser(Reader reader) {
            this.reader = reader;
        }

        @Override
        public int getExpectedNumberOfField() {
            return numberOfField;
        }

        @Override
        public AppointmentSlot instantiate(String... fields) throws ParserException {
            String owner = fields[0];
            String id = fields[1];

            Date begin = Helper.validateAndParseDate(fields[2]);
            Date end = Helper.validateAndParseDate(fields[3]);

            if (!Helper.validateAndGetDateInterval(begin, end, "begin time of appointment slot",
                    "end time of appointment slot")) {
                throw new ParserException(Helper.getErrorMessage());
            }
            AppointmentSlot.SlotType slotType =
                    fields[4] == null ? null : AppointmentSlot.SlotType.valueOf(fields[4]);
            AppointmentSlot.ParticipatorType participatorType =
                    fields[5] == null ? null : AppointmentSlot.ParticipatorType.valueOf(fields[5]);
            String participatorIdentifier = fields[6];
            AppointmentSlot toReturn = new AppointmentSlot(owner, id, begin, end, slotType, participatorType, participatorIdentifier);
            return toReturn;
        }
    }
}
