package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.InvokeMainTestCase;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * An integration test for {@link Project4} that invokes its main method with
 * various arguments
 */
@TestMethodOrder(MethodName.class)
class Project4IT extends InvokeMainTestCase {
    private static final String HOSTNAME = "localhost";
    private static final String PORT = "8080";

    @Test
    void test1NoCommandLineArguments() {
        MainMethodResult result = invokeMain(Project4.class);
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("host and port must be specified"));
    }

    @Test
    void test2EmptyServer() {
        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, "IT test");
        assertThat(result.getExitCode(), equalTo(1));
        String out = result.getTextWrittenToStandardError();
        assertThat(out, containsString("Cannot find any appointment with owner IT test"));
    }

    @Test
    void test3NoDefinitionsThrowsAppointmentBookRestException() {
        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, "-search", "IT test",
                "2/3/2020", "2:30", "pm", "2/3/2020", "3:00", "pm");
        assertThat(result.getExitCode(), equalTo(1));
        String out = result.getTextWrittenToStandardError();
        assertThat(out, containsString(
                "Cannot find any appointment that begins between 2/3/2020 2:30 pm and 2/3/2020 3:00 pm"));
    }

    @Test
    void test4AddAppointment() {
        String owner = "IT test";
        String description = "add appt in IT test";

        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, owner, description,
                "2/3/2020", "2:30", "pm", "2/3/2020", "3:00", "pm");
        assertThat(result.getExitCode(), equalTo(0));

        result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, owner);
        assertThat(result.getTextWrittenToStandardOut(), containsString("IT test"));
        assertThat(result.getTextWrittenToStandardOut(), containsString(description));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/3/20, 2:30 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/3/20, 3:00 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("30 minutes"));
    }

    @Test
    void test5AddAnotherAppointment() {
        String owner = "IT test";
        String description = "another appt in IT test";

        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, owner, description,
                "2/2/2020", "5:30", "pm", "2/2/2020", "7:00", "pm");
        assertThat(result.getExitCode(), equalTo(0));

        result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, owner);
        assertThat(result.getTextWrittenToStandardOut(), containsString("IT test"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("add appt in IT test"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/3/20, 2:30 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/3/20, 3:00 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("30 minutes"));
        assertThat(result.getTextWrittenToStandardOut(), containsString(description));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/2/20, 5:30 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/2/20, 7:00 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("30 minutes"));
    }

    @Test
    void test6SearchAppointment() {
        String owner = "IT test";
        String description = "another appt in IT test";

        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-search", "-port", PORT, owner,
                "2/2/2020", "3:30", "pm", "2/3/2020", "9:00", "pm");
        assertThat(result.getExitCode(), equalTo(0));

        result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, owner);
        assertThat(result.getTextWrittenToStandardOut(), containsString("IT test"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("add appt in IT test"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/3/20, 2:30 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/3/20, 3:00 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("30 minutes"));
        assertThat(result.getTextWrittenToStandardOut(), containsString(description));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/2/20, 5:30 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/2/20, 7:00 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("90 minutes"));
    }

    @Test
    void test7SearchAppointmentAgain() {
        String owner = "IT test";
        String description = "another appt in IT test";

        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-search", "-port", PORT, owner,
                "2/2/2020", "3:30", "pm", "2/2/2020", "9:00", "pm");
        assertThat(result.getExitCode(), equalTo(0));

        result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, owner);
        assertThat(result.getTextWrittenToStandardOut(), containsString("IT test"));
        assertThat(result.getTextWrittenToStandardOut(), containsString(description));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/2/20, 5:30 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/2/20, 7:00 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("90 minutes"));
    }

    @Test
    void test8AddAppointmentAndPrint() {
        String owner = "IT test";
        String description = "should print";

        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-print", "-port", PORT, owner,
                description, "4/15/2020", "2:00", "pm", "4/15/2020", "3:00", "pm");
        assertThat(result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardOut(), containsString(description));
        assertThat(result.getTextWrittenToStandardOut(), containsString("4/15/2020 2:00 pm"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("4/15/2020 3:00 pm"));
    }

    @Test
    void test9Handle404Gracefully() {
        String owner = "IT test ???";

        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, owner);
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("Cannot find any appointment with owner"));
    }

    @Test
    void test10Handle404Gracefully() {
        String owner = "IT test";

        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-search", "-port", PORT, owner,
                "6/2/2020", "3:30", "pm", "6/2/2020", "9:00", "pm");
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(),
                containsString("Cannot find any appointment that begins between "));
    }

    @Test
    void test11HandleUnparseablePort() {
        String owner = "IT test";

        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-search", "-port", "fwoejrweo", owner,
                "6/2/2020", "3:30", "pm", "6/2/2020", "9:00", "pm");
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(),
                containsString("it cannot be parsed as an integer for port number"));
    }

    @Test
    void test12HandleUnconnectedHost() {
        String owner = "IT test";

        MainMethodResult result = invokeMain(Project4.class, "-host", "dfgsdfgdf", "-search", "-port", "8080", owner,
                "6/2/2020", "3:30", "pm", "6/2/2020", "9:00", "pm");
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(),
                containsString("While connecting to host, "));
    }

    @Test
    void test13HandleInvalidArgument() {
        String owner = "IT test";

        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-search", "-port", PORT, owner,
                "6/XX/2020", "3:30", "pm", "6/2/2020", "9:00", "pm");
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("Unparseable date"));
    }

    @Test
    void test14HandleInvalidArgument() {
        String owner = "    ";

        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-search", "-port", PORT, owner,
                "6/1/2020", "3:30", "pm", "6/2/2020", "9:00", "pm");
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("Owner is either missing or invalid"));
    }

    @Test
    void test15AddAppointmentWithAnotherOwner() {
        String owner = "Another IT test";
        String description = "IT test 15";

        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, owner, description,
                "12/13/2020", "2:30", "am", "12/13/2020", "3:00", "am");
        assertThat(result.getExitCode(), equalTo(0));

        result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, owner);
        assertThat(result.getTextWrittenToStandardOut(), containsString(owner));
        assertThat(result.getTextWrittenToStandardOut(), containsString(description));
        assertThat(result.getTextWrittenToStandardOut(), containsString("12/13/20, 2:30 AM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("12/13/20, 3:00 AM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("30 minutes"));
    }

    @Test
    void test16AddAnotherAppointmentWithAnotherOwner() {
        String owner = "Another IT test";
        String description = "IT test 16";

        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, owner, description,
                "1/2/2021", "5:30", "pm", "1/2/2021", "7:00", "pm");
        assertThat(result.getExitCode(), equalTo(0));

        result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, owner);
        assertThat(result.getTextWrittenToStandardOut(), containsString(owner));
        assertThat(result.getTextWrittenToStandardOut(), containsString("IT test 15"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("12/13/20, 2:30 AM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("12/13/20, 3:00 AM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("30 minutes"));
        assertThat(result.getTextWrittenToStandardOut(), containsString(description));
        assertThat(result.getTextWrittenToStandardOut(), containsString("1/2/21, 5:30 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("1/2/21, 7:00 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("90 minutes"));
    }

    @Test
    void test17SearchAppointmentForAnotherOwner() {
        String owner = "Another IT test";
        String description = "IT test 16";

        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-search", "-port", PORT, owner,
                "12/13/2020", "2:28", "am", "1/2/2021", "5:31", "pm");
        assertThat(result.getExitCode(), equalTo(0));

        result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT, owner);
        assertThat(result.getTextWrittenToStandardOut(), containsString(owner));
        assertThat(result.getTextWrittenToStandardOut(), containsString("IT test 15"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("12/13/20, 2:30 AM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("12/13/20, 3:00 AM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("30 minutes"));
        assertThat(result.getTextWrittenToStandardOut(), containsString(description));
        assertThat(result.getTextWrittenToStandardOut(), containsString("1/2/21, 5:30 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("1/2/21, 7:00 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("90 minutes"));
    }
}