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
        MainMethodResult result = invokeMain( Project4.class );
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("host and port must be specified"));
    }

    @Test
    void test2EmptyServer() {
        MainMethodResult result = invokeMain( Project4.class, "-host", HOSTNAME, "-port", PORT, "IT test" );
        assertThat(result.getExitCode(), equalTo(1));
        String out = result.getTextWrittenToStandardError();
        assertThat(out, containsString("Cannot find any appointment with owner IT test"));
    }

    @Test
    void test3NoDefinitionsThrowsAppointmentBookRestException() {
        MainMethodResult result = invokeMain( Project4.class, "-host", HOSTNAME, "-port", PORT, "-search", 
            "IT test", "2/3/2020", "2:30", "pm", "2/3/2020", "3:00", "pm" );
        assertThat(result.getExitCode(), equalTo(1));
        String out = result.getTextWrittenToStandardError();
        assertThat(out, containsString(
            "Cannot find any appointment that begins between 2/3/2020 2:30 pm and 2/3/2020 3:00 pm"));
    }

    @Test
    void test4AddDefinition() {
        String owner = "IT test";
        String description = "add appt in IT test";

        MainMethodResult result = invokeMain( Project4.class, "-host", HOSTNAME, "-port", PORT, owner, description,
            "2/3/2020", "2:30", "pm", "2/3/2020", "3:00", "pm");
        assertThat(result.getExitCode(), equalTo(0));

        result = invokeMain( Project4.class, "-host", HOSTNAME, "-port", PORT, owner );
        assertThat(result.getTextWrittenToStandardOut(), containsString("IT test"));
        assertThat(result.getTextWrittenToStandardOut(), containsString(description));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/3/20, 2:30 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("2/3/20, 3:00 PM"));
        assertThat(result.getTextWrittenToStandardOut(), containsString("30 minutes"));
    }
}