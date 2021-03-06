package edu.pdx.cs410J.jscott.client;

import com.google.common.annotations.VisibleForTesting;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A basic GWT class that makes sure that we can send an airline back from the server
 */
public class AirlineGwt implements EntryPoint {

  private final Alerter alerter;
  private final AirlineServiceAsync airlineService;
  private final Logger logger;

  @VisibleForTesting
  Button showAirlineButton;

  @VisibleForTesting
  Button postFlightButton;

  @VisibleForTesting
  Button showUndeclaredExceptionButton;

  @VisibleForTesting
  Button showDeclaredExceptionButton;

  @VisibleForTesting
  Button showClientSideExceptionButton;

  @VisibleForTesting
  Button showHelpMenuButton;

  @VisibleForTesting
  TextBox boxForFlightInfo;

  @VisibleForTesting
  Button searchForFlightButton;

  @VisibleForTesting
  TextBox boxForFlightSearch;

  RichTextArea textArea;

  public AirlineGwt() {
    this(new Alerter() {
      @Override
      public void alert(String message) {
        Window.alert(message);
      }
    });
  }

  @VisibleForTesting
  AirlineGwt(Alerter alerter) {
    this.alerter = alerter;
    this.airlineService = GWT.create(AirlineService.class);
    this.logger = Logger.getLogger("airline");
    Logger.getLogger("").setLevel(Level.INFO);  // Quiet down the default logging
  }

  private void alertOnException(Throwable throwable) {
    Throwable unwrapped = unwrapUmbrellaException(throwable);
    StringBuilder sb = new StringBuilder();
    sb.append(unwrapped.toString());
    sb.append('\n');

    for (StackTraceElement element : unwrapped.getStackTrace()) {
      sb.append("  at ");
      sb.append(element.toString());
      sb.append('\n');
    }

    this.alerter.alert(sb.toString());
  }

  private Throwable unwrapUmbrellaException(Throwable throwable) {
    if (throwable instanceof UmbrellaException) {
      UmbrellaException umbrella = (UmbrellaException) throwable;
      if (umbrella.getCauses().size() == 1) {
        return unwrapUmbrellaException(umbrella.getCauses().iterator().next());
      }

    }

    return throwable;
  }

  private void addWidgets(VerticalPanel panel) {
    
    Label title = new Label("Airline Application");
    
    showAirlineButton = new Button("Show Airline");
    showAirlineButton.setPixelSize(200, 50);
    showAirlineButton.setFocus(true);
    showAirlineButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        showAirline();
      }
    });

    postFlightButton = new Button("Add Flight");
    postFlightButton.setPixelSize(200, 50);
    postFlightButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        postFlight(boxForFlightInfo.getText());
      }
    });

    searchForFlightButton = new Button("Search Flights");
    searchForFlightButton.setPixelSize(200, 50);
    searchForFlightButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        searchFlights(boxForFlightSearch.getText());
      }
    });




    showUndeclaredExceptionButton = new Button("Show undeclared exception");
    showUndeclaredExceptionButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        showUndeclaredException();
      }
    });

    showDeclaredExceptionButton = new Button("Show declared exception");
    showDeclaredExceptionButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        showDeclaredException();
      }
    });

    showClientSideExceptionButton= new Button("Show client-side exception");
    showClientSideExceptionButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        throwClientSideException();
      }
    });

    showHelpMenuButton = new Button("Help");
    showHelpMenuButton.setPixelSize(200, 50);
    showHelpMenuButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        showHelpMenu();
      }
    });

    Label blankSpaceLabel = new Label("    ");
    Label labelForPostFlightBox = new Label("To enter a flight type the flight info in this Box in the following format:");
    Label labelforPostFlightBox2 = new Label("AirlineName FlightNumber Source DepartureDate DestinationAirport DestinationDate ");
    boxForFlightInfo = new TextBox();
    boxForFlightInfo.setVisibleLength(70);
    Label labelForFlightSearchBox = new Label("Enter Source and Destination Airports Here:");
    boxForFlightSearch = new TextBox();
    boxForFlightSearch.setVisibleLength(27);

    textArea = new RichTextArea();
    textArea.setWidth("500px");


    panel.add(title);
    panel.add(showAirlineButton);
    panel.add(blankSpaceLabel);

    panel.add(labelForPostFlightBox);
    panel.add(labelforPostFlightBox2);
    panel.add(boxForFlightInfo);
    panel.add(postFlightButton);


    panel.add(labelForFlightSearchBox);
    panel.add(boxForFlightSearch);
    panel.add(searchForFlightButton);

    /*
    panel.add(showUndeclaredExceptionButton);
    panel.add(showDeclaredExceptionButton);
    panel.add(showClientSideExceptionButton);
    */

    panel.add(showHelpMenuButton);
    panel.add(textArea);

  }

  private void searchFlights(String text) {
    if( text.equalsIgnoreCase("")){
      alerter.alert("Please enter flight info in text box in the format <src dest> for example PDX LAX");
      return;
    }
    logger.info("Searching for matching flights");
    airlineService.searchFlights(text, new AsyncCallback<String >() {
      @Override
      public void onFailure(Throwable throwable) {
        alerter.alert(throwable.getMessage());
      }

      @Override
      public void onSuccess(String flights) {
        textArea.setText(flights);
      }
    });
  }

  private void postFlight(String flight) {
    if( flight.equalsIgnoreCase("")){
      alerter.alert("Please enter flight info in text box");
      return;
    }
    logger.info("Posting a flight object to the Airline Server");
    final String aflight = flight;
    airlineService.postFlight(flight, new AsyncCallback<Void>(){

              @Override
              public void onFailure(Throwable throwable) {
                alerter.alert(throwable.getMessage());
              }

              @Override
              public void onSuccess(Void aVoid) {
                textArea.setText("Added flight: " + aflight);
              }
            }
    );
  }

  private void throwClientSideException() {
    logger.info("About to throw a client-side exception");
    throw new IllegalStateException("Expected exception on the client side");
  }

  private void showUndeclaredException() {
    logger.info("Calling throwUndeclaredException");
    airlineService.throwUndeclaredException(new AsyncCallback<Void>() {
      @Override
      public void onFailure(Throwable ex) {
        alertOnException(ex);
      }

      @Override
      public void onSuccess(Void aVoid) {
        alerter.alert("This shouldn't happen");
      }
    });
  }

  private void showDeclaredException() {
    logger.info("Calling throwDeclaredException");
    airlineService.throwDeclaredException(new AsyncCallback<Void>() {
      @Override
      public void onFailure(Throwable ex) {
        alertOnException(ex);
      }

      @Override
      public void onSuccess(Void aVoid) {
        alerter.alert("This shouldn't happen");
      }
    });
  }

  private void showAirline() {
    logger.info("Calling getAirline");
    airlineService.getAirline(new AsyncCallback<String>() {

      @Override
      public void onFailure(Throwable ex) {
        alerter.alert(ex.getMessage());
      }

      @Override
      public void onSuccess(String airline) {
        textArea.setText(airline);
      }
    });
  }

  private void showHelpMenu() {
    logger.info("Calling getHelpMenu");
    airlineService.getHelpMenu(new AsyncCallback<String>() {
      @Override
      public void onFailure(Throwable throwable) {
        alertOnException(throwable);
      }

      @Override
      public void onSuccess(String aString) {
        textArea.setText(aString);
      }
    });
  }

  @Override
  public void onModuleLoad() {
    setUpUncaughtExceptionHandler();

    // The UncaughtExceptionHandler won't catch exceptions during module load
    // So, you have to set up the UI after module load...
    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
      @Override
      public void execute() {
        setupUI();
      }
    });

  }

  private void setupUI() {
    RootPanel rootPanel = RootPanel.get();
    VerticalPanel panel = new VerticalPanel();

    rootPanel.add(panel);
    addWidgets(panel);
  }

  private void setUpUncaughtExceptionHandler() {
    GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
      @Override
      public void onUncaughtException(Throwable throwable) {
        alertOnException(throwable);
      }
    });
  }

  @VisibleForTesting
  interface Alerter {
    void alert(String message);
  }
}
