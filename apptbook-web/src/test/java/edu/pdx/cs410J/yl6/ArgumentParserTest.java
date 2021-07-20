package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

public class ArgumentParserTest {

  static String README = "readme";
  static String USAGE = "usage";

  ArgumentParser constructThreeOptionsWithSixArgs(int n1, int n2, int n3) {
    ArgumentParser argparser = new ArgumentParser()
        .addOption("-foo", n1).addOption("-bar", n2).addOption("-baz", n3)
        .setReadme(README)
        .setUsage(USAGE);
    return argparser;
  }

  @Test
  void testCase1() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 4);
    String[] passin = { 
      "-bar", "bar1", "-foo", "-baz", "baz1", "baz2", "baz3", "baz4",
      "a1", "a2", "a3", "a4", "a5", "a6"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(true));
    assertThat(argparser.isEnabled("-bar"), equalTo(true));
    assertThat(argparser.isEnabled("-baz"), equalTo(true));
    assertThat(argparser.isEnabled("-foo"), equalTo(true));
  }

  @Test
  void testCase2() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 4);
    String[] passin = { 
      "-baz", "baz1", "baz2", "baz3", "baz4", "-foo", "-bar", "bar1",
      "a1", "a2", "a3", "a4", "a5", "a6"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(true));
    assertThat(argparser.isEnabled("-bar"), equalTo(true));
    assertThat(argparser.isEnabled("-baz"), equalTo(true));
    assertThat(argparser.isEnabled("-foo"), equalTo(true));
  }

  @Test
  void testCaseReadme1() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 4);
    String[] passin = { 
      "-baz", "baz1", "baz2", "baz3", "baz4", 
      "-foo", 
      "-bar", "bar1", 
      "-README", 
      "a1", "a2", "a3", "a4", "a5", "a6"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(false));
    assertThat(argparser.getErrorMessage(), equalTo(README));
  }

  @Test
  void testCaseReadme2() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 4);
    String[] passin = { 
      "-baz", "baz1", "baz2", "baz3", "baz4", 
      "-README",
      "-foo", 
      "-bar", "bar1", 
      "a1", "a2", "a3", "a4", "a5", "a6"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(false));
    assertThat(argparser.getErrorMessage(), equalTo(README));
  }

  /**
   * This test case demonstrates that once a duplicated option occurs, its followed 
   * option should not be treated as its arguments, we check if README occurs behind
   * the error option.
   */
  @Test
  void testCaseReadmeWithDuplicateOption1() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 4);
    String[] passin = { 
      "-baz", "baz1", "baz2", "baz3", "baz4", 
      "-baz", "baz1", "baz2",
      "-README",  // print readme even the 2nd baz has not enough arguments
      "-foo", 
      "-bar", "bar1", 
      "a1", "a2", "a3", "a4", "a5", "a6"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(false));
    assertThat(argparser.getErrorMessage(), equalTo(README));
  }

  @Test
  void testCaseReadmeWithDuplicateOption2() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 4);
    String[] passin = { 
      "-baz", "baz1", "baz2", "baz3", "baz4", 
      "-README",  // print readme even the 2nd baz has not enough arguments
      "-foo", 
      "-bar", "bar1", 
      "-foo",
      "a1", "a2", "a3", "a4", "a5", "a6"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(false));
    assertThat(argparser.getErrorMessage(), equalTo(README));
  }

  @Test
  void testCaseReadmeWithDuplicateOption3() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 4);
    String[] passin = { 
      "-baz", "baz1", "baz2", "baz3", "baz4", 
      "-README",  
      "-foo", "-bar", "bar1", "-foo",
      "-foo", "-bar", "bar1", "-foo",
      "-foo", "-bar", "bar1", "-foo",
      "-foo", "-bar", "bar1", "-foo", // still print readme since it occurred
      "a1", "a2", "a3", "a4", "a5", "a6"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(false));
    assertThat(argparser.getErrorMessage(), equalTo(README));
  }

  @Test
  void testCaseReadmeWithDuplicateOption4() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 4);
    String[] passin = { 
      "-baz", "baz1", "baz2", "baz3", "baz4",   
      "-foo", "-bar", "bar1", "-foo",
      "-foo", "-bar", "bar1", "-foo",
      "-foo", "-bar", "bar1", "-foo",
      "-README",                      // still print readme since it occurred
      "-foo", "-bar", "bar1", "-foo", 
      "a1", "a2", "a3", "a4", "a5", "a6"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(false));
    assertThat(argparser.getErrorMessage(), equalTo(README));
  }

  @Test
  void testCaseReadmeWithDuplicateOption5() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 4);
    String[] passin = { 
      "-baz", "-README", "-README", "-README", "-README", // these readmes should be 
                                                          // treated as args of baz
      "-foo", "-bar", "-README", "-foo",  // this readme is arg of bar
      "-foo", "-bar", "-README", "-foo",  // since foo duplicates occur, 
                                          // we only look for readme behind the 
                                          // the first place error happens
                                          // so this readme is the option readme
      "a1", "a2", "a3", "a4", "a5", "a6"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(false));
    assertThat(argparser.getErrorMessage(), equalTo(README));
  }

  @Test
  void testCaseReadmeWithDuplicateOption6() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 4);
    String[] passin = { 
      "-baz", "-README", "-README", "-README", "-README", // these readmes should be 
                                                          // treated as args of baz
      "-foo", "-bar", "-README",  // bar is not duplicated, readme as arg 
      "-foo", "-bar", "-foo",     // foo duplicated, print error for duplication
      "a1", "a2", "a3", "a4", "a5", "a6"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(false));
    assertThat(argparser.getErrorMessage(), equalTo("duplicated -foo in options"));
  }

  @Test
  void testCaseReadmeWithDuplicateOption7() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 4);
    String[] passin = { 
      "-baz", "-baz", "-README", "-README", "-README", "-README", 
      // print readme since above the 2nd baz is the 1st arg of 1st baz
      // the last readme is the option readme                             
      "-foo", "-bar", "-README",  
      "-foo", "-bar", "-foo",     
      "a1", "a2", "a3", "a4", "a5", "a6"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(false));
    assertThat(argparser.getErrorMessage(), equalTo(README));
  }

  @Test
  void testCaseGetOptionArguments1() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(5, 8, 4);
    String[] passin = {                           
      "-foo", "foo1", "foo2", "foo3", "foo4", "foo5", 
      "-bar", "bar1", "bar2", "bar3", "bar4", "bar5", "bar6", "bar7", "bar8", 
      "-baz", "baz1", "baz2", "baz3", "baz4",    
      "a1", "a2", "a3", "a4", "a5", "a6"
    };
    String[] a1 = {"foo1", "foo2", "foo3", "foo4", "foo5"};
    String[] a2 = {"bar1", "bar2", "bar3", "bar4", "bar5", "bar6", "bar7", "bar8"};
    String[] a3 = {"baz1", "baz2", "baz3", "baz4"};
    String[] x1 = new String[5];
    String[] x2 = new String[8];
    String[] x3 = new String[4];
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(true));
    assertThat(argparser.isEnabled("-bar"), equalTo(true));
    assertThat(argparser.isEnabled("-baz"), equalTo(true));
    assertThat(argparser.isEnabled("-foo"), equalTo(true));
    x1 = argparser.getOptionArguments("-foo").toArray(x1);
    x2 = argparser.getOptionArguments("-bar").toArray(x2);
    x3 = argparser.getOptionArguments("-baz").toArray(x3);
    assertThat(x1, equalTo(a1));
    assertThat(x2, equalTo(a2));
    assertThat(x3, equalTo(a3));
  }

  @Test
  void testCaseGetOptionArguments1WithDiffOrder() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(5, 8, 4);
    String[] passin = {                            
      "-bar", "bar1", "bar2", "bar3", "bar4", "bar5", "bar6", "bar7", "bar8", 
      "-foo", "foo1", "foo2", "foo3", "foo4", "foo5", 
      "-baz", "baz1", "baz2", "baz3", "baz4",    
      "a1", "a2", "a3", "a4", "a5", "a6"
    };
    String[] a1 = {"foo1", "foo2", "foo3", "foo4", "foo5"};
    String[] a2 = {"bar1", "bar2", "bar3", "bar4", "bar5", "bar6", "bar7", "bar8"};
    String[] a3 = {"baz1", "baz2", "baz3", "baz4"};
    String[] x1 = new String[5];
    String[] x2 = new String[8];
    String[] x3 = new String[4];
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(true));
    assertThat(argparser.isEnabled("-bar"), equalTo(true));
    assertThat(argparser.isEnabled("-baz"), equalTo(true));
    assertThat(argparser.isEnabled("-foo"), equalTo(true));
    x1 = argparser.getOptionArguments("-foo").toArray(x1);
    x2 = argparser.getOptionArguments("-bar").toArray(x2);
    x3 = argparser.getOptionArguments("-baz").toArray(x3);
    assertThat(x1, equalTo(a1));
    assertThat(x2, equalTo(a2));
    assertThat(x3, equalTo(a3));
  }

  @Test
  void testNotEnoughOptionArgs() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 4);
    String[] passin = { 
      "-bar", "bar1", 
      "-foo", 
      "-baz", "baz1", "baz2"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(false));
    assertThat(argparser.getErrorMessage(), equalTo("Not enough arguments passed in for option -baz"));
  }

  @Test
  void testGetArguments() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 2);
    String[] passin = { 
      "-bar", "bar1", 
      "-foo", 
      "-baz", "baz1", "baz2", "a1", "a2", "a3"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(true));
    String[] args = argparser.getAllArguments();
    assertThat(args.length, equalTo(3));
    assertThat(args[0], equalTo("a1"));
    assertThat(args[1], equalTo("a2"));
    assertThat(args[2], equalTo("a3"));
  }

  @Test
  void testGetNotEnoughArguments() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 2);
    String[] passin = { 
      "-bar", "bar1", 
      "-foo", 
      "-baz", "baz1", "baz2", "a1", "a2", "a3"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(true));
    String[] args = argparser.getArguments("arg1","arg2","arg3","arg4","arg5");
    assertThat(args, equalTo(null));
    assertThat(argparser.getErrorMessage(), equalTo("Missing required argument(s):\n  * arg4\n  * arg5\n"));
  }

  @Test
  void testGetMoreThanNeededArguments() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 2);
    String[] passin = { 
      "-bar", "bar1", 
      "-foo", 
      "-baz", "baz1", "baz2", "a1", "a2", "a3", "a4", "a5"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(true));
    String[] args = argparser.getArguments("arg1","arg2","arg3");
    assertThat(args, equalTo(null));
    assertThat(argparser.getErrorMessage(), equalTo("The following argument(s) passed in are extraneous:\n  * a4\n  * a5\n"));
  }

  @Test
  void testGetNotEnoughArgumentsCase2() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 2);
    String[] passin = { 
      "-bar", "bar1", 
      "-foo", 
      "-baz", "baz1", "baz2"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(true));
    String[] args = argparser.getArguments("arg1","arg2","arg3");
    assertThat(args, equalTo(null));
    assertThat(argparser.getErrorMessage(), equalTo("Missing arguments\nusage"));
  }

  @Test
  void testGetNotEnoughArgumentsCase3() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 2);
    String[] passin = { 
      "-bar", "bar1", 
      "-foo", 
      "-baz", "baz1", "baz2", "a1", "a2", "a3"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(true));
    String[] args = argparser.getArguments("arg1","arg2","arg3");
    assertThat(args[0], equalTo("a1"));
    assertThat(args[1], equalTo("a2"));
    assertThat(args[2], equalTo("a3"));
  }

  @Test
  void testGetNamedArguments() {
    ArgumentParser argparser = constructThreeOptionsWithSixArgs(0, 1, 2);
    String[] passin = { 
      "-bar", "bar1", 
      "-foo", 
      "-baz", "baz1", "baz2", "a1", "a2", "a3"
    };
    boolean rt = argparser.parse(passin);
    assertThat(rt, equalTo(true));
    String[] args = argparser.getAllArguments();
    assertThat(args[0], equalTo("a1"));
    assertThat(args[1], equalTo("a2"));
    assertThat(args[2], equalTo("a3"));
  }
}
