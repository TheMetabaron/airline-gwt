package edu.pdx.cs410J.jscott.client;

import edu.pdx.cs410J.AbstractAirline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Airline extends AbstractAirline<Flight> {

  private String name;
  private List<Flight> flights = new ArrayList<>();

  /**
   * In order for GWT to serialize this class (so that it can be sent between
   * the client and the server), it must have a zero-argument constructor.
   */
  public Airline() {

  }

  public Airline(String airlineName) {
    name = airlineName;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void addFlight(Flight flight) {
    this.flights.add(flight);
    this.sort();
  }

  @Override
  public Collection<Flight> getFlights() {
    return this.flights;
  }


  public void sort() {
    Collections.sort(flights, new FlightComparator());
  }
}