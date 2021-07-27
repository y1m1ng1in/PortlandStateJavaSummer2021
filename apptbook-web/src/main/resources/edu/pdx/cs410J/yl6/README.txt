CS410/510 Advanced programming with Java Summer 2021, Project 4.

This software creates/searches an appointment book and appointments 
via command line arguments. The software communicates with RESTful 
api server based on command line arguments, either creates a new 
appointment and insert into the owner's appointment book; or gets
all the appointments belong to a owner; or searches appointments of 
a given owner that begin within a given time interval. If -print 
option presents when adding new appointment, the new created 
appointment output to the standard output. The software validates
command line arguments before sending actual HTTP requests to server,
if any validation violation occurs, then a error message outputs to
standard error and exits with status 1. Otherwise, if HTTP response
with status code 200, the program exits with code 0; but if any 
4XX, 5XX status code is returned, then the program outputs error
message to standard error, the program exits with code 1.

usage: java edu.pdx.cs410J.yl6.Project4 [options] <args>
  args are (in this order):
    owner               The person who owns the appt book
    description         A description of the appointment
    begin               When the appt begins 
    end                 When the appt ends
  options are (options may appear in any order):
    -host hostname      Host computer on which the server runs
    -port port          Port on which the server is listening
    -search             Appointments should be searched for
    -print              Prints a description of the new appointment
    -README             Prints a README for this project and exits
  Date and time should be in the format: mm/dd/yyyy hh:mm [am|pm] 
