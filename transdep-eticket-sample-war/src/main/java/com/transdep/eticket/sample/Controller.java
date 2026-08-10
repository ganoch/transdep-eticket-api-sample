package com.transdep.eticket.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transdep.eticket.Customer;
import com.transdep.eticket.Passenger;
import com.transdep.eticket.TransDepEticket;

@RestController
@RequestMapping("/api")
public class Controller {
  private static final Logger logger = LoggerFactory.getLogger(Controller.class);
  private TransDepEticket transDepEticketService;

  @Autowired
  public Controller() {
  }

  @PostConstruct
  public void init() throws Exception {
    this.transDepEticketService = new TransDepEticket();
  }

  @GetMapping("/home")
  public Map<String, List<Map<String, String>>> home() throws Exception {
    List<Map<String, String>> departures = this.transDepEticketService.fetchDepartures();

    Map<String, List<Map<String, String>>> response = new HashMap<>();
    response.put("departures", departures);

    logger.info("Received request at /home endpoint");
    return response;
  }

  @PostMapping("/setDeparture")
  public Map<String, List<Map<String, String>>> setDeparture(@RequestBody Map<String, Object> payload)
      throws Exception {
    this.transDepEticketService.setDeparture((String) payload.get("departure").toString());

    List<Map<String, String>> destinations = this.transDepEticketService.fetchDestinations();
    List<Map<String, String>> stops = this.transDepEticketService.fetchStops();

    Map<String, List<Map<String, String>>> response = new HashMap<>();
    response.put("departures", stops.isEmpty() ? destinations : null);
    response.put("stops", stops);

    logger.info("Received request at /setDeparture endpoint");
    return response;
  }

  @PostMapping("/setStop")
  public Map<String, List<Map<String, String>>> setStop(@RequestBody Map<String, Object> payload) throws Exception {
    this.transDepEticketService.setDeparture((String) payload.get("departure").toString());
    this.transDepEticketService.setStop((String) payload.get("stop").toString());

    List<Map<String, String>> destinations = this.transDepEticketService.fetchDestinations();

    Map<String, List<Map<String, String>>> response = new HashMap<>();
    response.put("destinations", destinations);

    logger.info("Received request at /setDeparture endpoint");
    return response;
  }

  @PostMapping("/setDestination")
  public Map<String, List<Map<String, String>>> setDestination(@RequestBody Map<String, Object> payload)
      throws Exception {
    this.transDepEticketService.setDeparture((String) payload.get("departure").toString());
    if (payload.containsKey("stop") && !((String) payload.get("stop").toString()).isEmpty()) {
      this.transDepEticketService.setStop((String) payload.get("stop").toString());
    }
    this.transDepEticketService.setDestination((String) payload.get("destination").toString());

    List<Map<String, String>> trips = this.transDepEticketService.fetchTrips();

    Map<String, List<Map<String, String>>> response = new HashMap<>();
    response.put("trips", trips);

    logger.info("Received request at /setDeparture endpoint");
    return response;
  }

  @PostMapping("/setTrip")
  public Map<String, Object> setTrip(@RequestBody Map<String, Object> payload) throws Exception {
    this.transDepEticketService.setDeparture((String) payload.get("departure").toString());
    this.transDepEticketService.setStop((String) payload.get("stop").toString());
    this.transDepEticketService.setDestination((String) payload.get("destination").toString());
    this.transDepEticketService.setDispatcherId((String) payload.get("trip").toString());

    return this.transDepEticketService.fetchSeatsData();
  }

  @PostMapping("/requestSeats")
  public Map<String, Object> requestSeats(@RequestBody Map<String, Object> payload) throws Exception {
    this.transDepEticketService.setDeparture((String) payload.get("departure").toString());
    this.transDepEticketService.setStop((String) payload.get("stop").toString());
    this.transDepEticketService.setDestination((String) payload.get("destination").toString());
    this.transDepEticketService.setDispatcherId((String) payload.get("trip").toString());

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> passengerMaps = (List<Map<String, Object>>) payload.get("passangers");
    List<Passenger> passengers = new ArrayList<>();
    for (Map<String, Object> passengerMap : passengerMaps) {
      Passenger passenger = new Passenger();
      passenger.setRegistryNum(passengerMap.get("registryNum") != null ? passengerMap.get("registryNum").toString() : null);
      passenger.setName(passengerMap.get("name") != null ? passengerMap.get("name").toString() : null);
      passenger.setInsurance(passengerMap.get("insurance") != null
          ? Integer.valueOf(passengerMap.get("insurance").toString())
          : 0);
      passenger.setSeat(passengerMap.get("seat") != null ? passengerMap.get("seat").toString() : null);
      passengers.add(passenger);
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> billTo = (Map<String, Object>) payload.get("billTo");

    Customer customer = new Customer();
    customer.setName(billTo.get("name") != null ? billTo.get("name").toString() : null);
    customer.setPhoneNum(billTo.get("phoneNum") != null ? billTo.get("phoneNum").toString() : null);
    customer.setEmail(billTo.get("email") != null ? billTo.get("email").toString() : null);
    customer.setCompanyReg(billTo.get("companyReg") != null ? billTo.get("companyReg").toString() : null);

    logger.info("Received request at /requestSeats endpoint");
    return this.transDepEticketService.requestSeats(passengers, customer);
  }

}
