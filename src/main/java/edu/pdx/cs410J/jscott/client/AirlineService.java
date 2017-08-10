package edu.pdx.cs410J.jscott.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.text.ParseException;

/**
 * A GWT remote service that returns a dummy airline
 */
@RemoteServiceRelativePath("airline")
public interface AirlineService extends RemoteService {

  /**
   * Returns the current date and time on the server
   */
  String getAirline();

  /**
   * Always throws an undeclared exception so that we can see GWT handles it.
   */
  void throwUndeclaredException();

  /**
   * Always throws a declared exception so that we can see GWT handles it.
   */
  void throwDeclaredException() throws IllegalStateException;

  /**
   * Displays the Help Menu
   */
  String getHelpMenu();

  /**
   * Posts a flight to the airline stored on the server.
   * @param flight
   */
  void postFlight(String flight) throws IllegalArgumentException;

  String searchFlights(String text) throws IllegalArgumentException;

}
