package edu.pdx.cs410J.jscott.client;

import com.google.gwt.i18n.client.DateTimeFormat;
import edu.pdx.cs410J.AbstractFlight;

import java.text.DateFormat;
import java.util.Date;

public class Flight extends AbstractFlight
{

  private int FlightNumber;
  private String Name;
  private String Src;
  private Date DepartTime;
  private String Dest;
  private Date ArriveTime;

  /**
   * In order for GWT to serialize this class (so that it can be sent between
   * the client and the server), it must have a zero-argument constructor.
   */
  public Flight() {
    Name = "Default";
    FlightNumber = 42;
    Src = "ABC";
    DepartTime = new Date();
    Dest = "ABC";
    ArriveTime = new Date();
  }
  /**
   * A constructor with parameters for each data field.
   * @param AirlineName The name of the airline
   * @param FlightValue The flight number and integer value
   * @param SourceAirportCode The three letter code departure airport
   * @param DepartureTime Departure date/time am/pm a date object in SHORT date format
   * @param DestinationCode The three letter code of the arrival airport
   * @param ArrivalTime The arrival date/time am/pm date object in SHORT date format
   */
  public Flight (String AirlineName, int FlightValue, String SourceAirportCode, Date DepartureTime, String DestinationCode, Date ArrivalTime){
    super();
    Name = AirlineName;
    FlightNumber = FlightValue;
    Src = SourceAirportCode;
    DepartTime = DepartureTime;
    Dest =  DestinationCode;
    ArriveTime = ArrivalTime;
  }

  @Override
  public int getNumber() {
    return FlightNumber;
  }

  @Override
  public String getSource() {
    return Src;
  }

  @Override
  public Date getDeparture() {
    return DepartTime;
  }

  /**
   * Returns a String of the Departure Time in format MM/dd/yyyy h:mm a.
   * Updated with the GWT version of DateTimeFormat
   * @return
   */
  public String getDepartureString() {
    DateTimeFormat fmt = DateTimeFormat.getFormat("MM/dd/yyyy h:mm a");
    return fmt.format(DepartTime);
    //int f = DateFormat.SHORT;
    //DateFormat df = DateFormat.getDateTimeInstance(f, f);
    //return df.format(DepartTime);
  }

  public String getDestination() {
    return Dest;
  }

  /**
   * Returns a String of the Arrival Time in format MM/dd/yyyy h:mm a.
   * Updated with the GWT version of DateTimeFormat
   * @return
   */
  public Date getArrival() {
    return ArriveTime;
  }

  public String getArrivalString() {
    DateTimeFormat fmt = DateTimeFormat.getFormat("MM/dd/yyyy h:mm a");
    return fmt.format(ArriveTime);
  }

  /**
   * Returns a brief textual description of this flight.
   */
  @Override
  public String toString() {
    return "Flight " + Integer.toString(this.getNumber()) + " departs " + this.getSource()
            + " at " + this.getDeparture().toString() + " arrives " +
            this.getDestination() + " at " + this.getArrival().toString();
  }
}
