package edu.pdx.cs410J.jscott.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.jscott.client.Airline;
import edu.pdx.cs410J.jscott.client.Flight;
import edu.pdx.cs410J.jscott.client.AirlineService;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * The server-side implementation of the Airline service
 *
 * REMEMBER ITS OK TO USE ALL OF JAVA INCLUDING PRETTY PRINTER ON THE SERVER SIDE
 */
public class AirlineServiceImpl extends RemoteServiceServlet implements AirlineService
{
  private Airline airline;

  @Override
  public String getAirline() {
    if (airline == null){
      return "There is no flight information on file";
    }
    PrettyPrinter prettyPrinter = new PrettyPrinter();
    prettyPrinter.dump(airline);
    return prettyPrinter.getPrettyText();

  }

  @Override
  public void throwUndeclaredException() {
    throw new IllegalStateException("Expected undeclared exception");
  }

  @Override
  public void throwDeclaredException() throws IllegalStateException {
    throw new IllegalStateException("Expected declared exception");
  }

  /**
   * Log unhandled exceptions to standard error
   *
   * @param unhandled
   *        The exception that wasn't handled
   */
  @Override
  protected void doUnexpectedFailure(Throwable unhandled) {
    unhandled.printStackTrace(System.err);
    super.doUnexpectedFailure(unhandled);
  }

  @Override
  public String getHelpMenu(){
    StringBuilder sb = new StringBuilder();

    sb.append("***********************************************************\n")
            .append("* README FOR PROJECT FIVE: A RICH INTERNET APPLICATION FOR AN AIRLINE *\n")
            .append("***********************************************************\n");
    sb.append("This program was written by Scott Jones for CS510J\n");
    sb.append("The purpose of this program is to simulate an Airline booking application.\n" +
            "The current functionality includes the ability to enter the information for\n" +
            "a flight in a text box and that information will be entered into the\n" +
            "fundamental Airline and Flight objects on the server.\n" +
            "In this project you will extend your airline application to support an airline " +
            "server that provides REST-ful web services to an airline client.\n" +
            "The list of Flights is ordered based on Departure airport and Departure time.\n");
    sb.append("USAGE\n\n" +
            "java -jar target/airline.jar [options] <args>\n\n" +
            "This program expects the following arguments in the order listed\n" +
            "name\t\t\tThe name of the airline\n" +
            "flightNumber\t\tThe flight number\n" +
            "src\t\t\tThree letter code of the departure airport\n" +
            "departTime\t\tTThe departure date and time (see note)\n" +
            "dest\t\t\tThree letter code of the arrival airport\n" +
            "arriveTime\t\tArrival date and time (see note)\n");
    sb.append("note: Date and time should be in the format: mm/dd/yyyy hh:mm am/pm");
    sb.append("\nOptions:\n" +
            "-host hostname\t\tHost computer on which the server runs\n" +
            "-port port \t\tPort on which the server is listening\n" +
            "-print\t\t\tPrints a description of the new flight\n" +
            "-README\t\t\tPrints a README for this project and exits\n" +
            "-search\t\t\tSearch for flights\n" +
            "If the -search option is provided, only the name, src and dest \n" +
            "are required. The program will print  out all of the direct flights " +
            "that originate at the src airport and terminate at the dest airport.\n");
    return sb.toString();
  }

  /**
   * Takes a string, parses the string into a flight object and adds it to the Airline
   * @param flight as a String
   */
  @Override
  public void postFlight(String flight) throws IllegalArgumentException{
    StringTokenizer st = new StringTokenizer(flight, " ");
    String [] args = new String [6];
    int i = 0;

    if(st.countTokens() != 10){
      //incorrect number of arguments
      throw new IllegalArgumentException("Incorrect Number of arguments");
    }

    while(st.hasMoreTokens()){
      args[i] = st.nextToken();
      /*if(args[1].contains("\"")){
        args[i] = args[i].substring(1);
        args[i] = args[i] + st.nextToken("\"");
        }
      */
      if(i ==3 || i == 5){
        args[i] = args[i] + " " + st.nextToken() + " " + st.nextToken();
      }
      ++i;
      if(i == 6){
        continue;
      }
      }

    checkCommandLineArguments(args);

    //TODO: USE DateTimeFormat (shared)?? so its the same on client and server?
    DateFormat format = new SimpleDateFormat();
    Date departureDate = new Date();
    Date arrivalDate = new Date();
    try {
      departureDate = format.parse(args[3]);
      arrivalDate = format.parse(args[5]);
    }catch(ParseException ex){
      throw new IllegalArgumentException("Caught Parse exception when translating dates");
    }

    //Create new airline if first flight
    if(airline == null){
      airline = new Airline(args[0]);
    }
    else if(!(airline.getName().equalsIgnoreCase(args[0]))){
      throw new IllegalArgumentException("Airline names don't match");
    }

    Flight newFlight = new Flight(args[0], Integer.valueOf(args[1]), args[2], departureDate, args[4], arrivalDate);
    airline.addFlight(newFlight);
  }

  private static void checkCommandLineArguments(String[] commands) throws IllegalArgumentException{
    //check to make sure airport code is 3 letter (2 and 4)
    if (!commands[2].matches("[a-zA-z]{3}") || !commands[4].matches("[a-zA-z]{3}")) {
      throw new IllegalArgumentException("Error: The airport codes must consist of three letters\nYour entries were: "
              + commands[2] + " and " + commands[4]);
    } else {
      commands[2] = commands[2].toUpperCase();
      commands[4] = commands[4].toUpperCase();
    }
  }
}
