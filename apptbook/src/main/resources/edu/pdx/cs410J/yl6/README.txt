CS410/510 Advanced programming with Java Summer 2021, Project 3.

This software creates an appointment book and a appointment via command 
line arguments. The software provides four options with users: one is to 
print the appointment with parameters specified by user; one is to import
and export appointment book from external file; one is to print texturally 
nicely-formatted appointment book to standard output or file; the other is 
to print the readme information. The software validates the command line 
arugments passed in, and reports any error to standard error and exits the 
program with status 1; if the number of arguments is correct, and the 
arguments are valid, and there is no error occurred during parsing/dumping 
appointment book, then the program creates the appointment and exits with 
status 0.

usage: java edu.pdx.cs410J.yl6.Project3 [options] <args>
  args are (in this order):
    owner               The person who owns the appt book
    description         A description of the appointment
    begin               When the appt begins (24-hour time)
    end                 When the appt ends (24-hour time)
  options are (options may appear in any order):
    -pretty file        Pretty print the appointment book to
                        a text file or standard out (file -)
    -textFile file      Where to read/write the appointment book
    -print              Prints a description of the new appointment
    -README             Prints a README for this project and exits
  Date and time should be in the format: mm/dd/yyyy hh:mm [am|pm] 
