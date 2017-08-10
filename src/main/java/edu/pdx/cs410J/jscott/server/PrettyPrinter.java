package edu.pdx.cs410J.jscott.server;

import edu.pdx.cs410J.AirlineDumper;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirlineDumper;
import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.jscott.client.Flight;

import java.io.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by scottjones on 7/26/17.
 * This class implements the AirlineDumper class with an updated dump method that outputs data in a more readable fashion.
 */
public class PrettyPrinter implements AirlineDumper {

    private StringBuilder writer = new StringBuilder();



    @Override
    public void dump(AbstractAirline airline) {

        int format = DateFormat.SHORT;
        DateFormat df = DateFormat.getDateTimeInstance(format, format);
        //return df.format(DepartTime);

        //Write Airline to the file
        writer.append("*************************************************\n");
        writer.append("Schedule of Flights for: ");
        writer.append(airline.getName() + "\n");
        writer.append("*************************************************\n");
        ArrayList<Flight> flights = new ArrayList<>(airline.getFlights());
        for (Flight f : flights) {
            writer.append("Flight Number:       ");
            writer.append(String.valueOf(f.getNumber())).append("\n");
            writer.append("Departure Airport:   ");
            writer.append(AirportNames.getName(f.getSource()) + "\n");
            writer.append("Departure Date:      ");
            writer.append(df.format(f.getDeparture()) + "\n");
            writer.append("Destination Airport: ");
            writer.append(AirportNames.getName(f.getDestination()) + "\n");
            writer.append("Arrival Date:        ");
            writer.append(df.format(f.getArrival()) + "\n");
            writer.append("-------------------------------------------------\n");
        }
    }

    public String getPrettyText(){
        return writer.toString();
    }
}