package edu.pdx.cs410J.jscott.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.jscott.client.Airline;
import edu.pdx.cs410J.jscott.client.Flight;
import edu.pdx.cs410J.jscott.client.AirlineService;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
  public String searchFlights(String text){
    if(airline == null){
      return "There is no flight information on file";
    }

    //Parse Airports
    StringTokenizer st = new StringTokenizer(text, " ");
    String source;
    String dest;
    if(st.countTokens() != 2){
      throw new IllegalArgumentException("To search for flights enter two three digit airport codes eg: PDX LAX");
    }
    source = st.nextToken();
    dest = st.nextToken();
    if(!source.matches("[a-zA-z]{3}") || !dest.matches("[a-zA-z]{3}")){
      throw new IllegalArgumentException("To search for flights enter two three digit airport codes eg: PDX LAX\"");
    }

    int format = DateFormat.SHORT;
    DateFormat df = DateFormat.getDateTimeInstance(format, format);
    boolean isThereAMatch = false;
    long duration = 0;
    StringBuilder sb = new StringBuilder();
    ArrayList<Flight> flights = new ArrayList<>(airline.getFlights());
    for(Flight f: flights){
      if (source.equalsIgnoreCase(f.getSource()) && dest.equalsIgnoreCase(f.getDestination())) {
        isThereAMatch = true;
        sb.append("Flight Number:       ").append(f.getNumber());
        sb.append("\nDeparture Airport:   ").append(AirportNames.getName(f.getSource()));
        sb.append("\nDeparture Date:      ").append(df.format(f.getDeparture()));
        sb.append("\nDestination Airport: ").append(AirportNames.getName(f.getDestination()));
        sb.append("\nArrival Date:        ").append(df.format(f.getArrival()));
        duration = f.getArrival().getTime() - f.getDeparture().getTime();
        duration = duration /1000/60;
        sb.append("\nFlight Duration:     ").append(duration).append(" minutes");
        sb.append("\n-------------------------------------------------\n");
      }
    }
    if(!isThereAMatch){
      sb.append("No matches found");
    }

    return sb.toString();
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
            .append("*               README FOR PROJECT FIVE:                  *\n" +
                    "*      A RICH INTERNET APPLICATION FOR AN AIRLINE         *\n")
            .append("***********************************************************\n");
    sb.append("This program was written by Scott Jones for CS510J\n");
    sb.append("The purpose of this program is to simulate an Airline booking application.\n" +
            "The functionality includes the ability to enter the information for\n" +
            "a flight in a text box and that information will be entered into the\n" +
            "The list of Flights is ordered based on Departure airport and Departure time.\n " +
            "The Show Airline button displays all flights entered for the airline so far. " +
            "The Add Flight button adds the flight data entered in the text box. If the information" +
            "matches the USAGE, the flight is added to the list of flights for that airline. " +
            "The Search Flight button searches for all flights that match the source and " +
            "desination airports. ");
    sb.append("USAGE\n\n" +
            "Add Flight Text box:" +
            "name flightNumber src departTime dest arriveTime\n\n" +
            "This program expects the following arguments in the order listed\n" +
            "name\t\t\tThe name of the airline\n" +
            "flightNumber\t\tThe flight number\n" +
            "src\t\t\tThree letter code of the departure airport\n" +
            "departTime\t\tThe departure date and time (see note)\n" +
            "dest\t\t\tThree letter code of the arrival airport\n" +
            "arriveTime\t\tArrival date and time (see note)\n");
    sb.append("note: Date and time should be in the format: mm/dd/yyyy hh:mm am/pm");
    sb.append("\n\nSearch Flights text box:" +
            "src dest" +
            "Only the three digit src and dest codes will be accepted.  \n" +
            "The program will print  out all of the direct flights " +
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
    char firstChar;

    if(st.countTokens() < 10 || st.countTokens() > 11){
      //incorrect number of arguments
      throw new IllegalArgumentException("Incorrect Number of arguments");
    }
    firstChar = flight.charAt(0);
    if(st.countTokens() != 10 && firstChar != '\"'){
      throw new IllegalArgumentException("Incorrect Number of arguments");
    }

    while(st.hasMoreTokens()){
      args[i] = st.nextToken();
      //TODO: Add capability to handle longer airline name
      firstChar = args[i].charAt(0);
      if(firstChar == '\"'){
        args[i] = args[i].substring(1);
        args[i] = args[i] + st.nextToken("\"");
        st.nextToken(" ");
        }

      if(i ==3 || i == 5){
        args[i] = args[i] + " " + st.nextToken() + " " + st.nextToken();
      }
      ++i;
      if(i == 6){
        continue;
      }
    }

    checkCommandLineArguments(args);

    DateFormat format = new SimpleDateFormat();
    Date departureDate = new Date();
    Date arrivalDate = new Date();
    try {
      departureDate = format.parse(args[3]);
      arrivalDate = format.parse(args[5]);
    }catch(ParseException ex){
      throw new IllegalArgumentException("Invalid input: Dates must be entered in the following format: MM/DD/YYYY HH:MM AM/PM");
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
