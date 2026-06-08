package com.transdep.eticket.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api")
public class Controller {
  private static final Logger logger = LoggerFactory.getLogger(Controller.class);

  @Autowired
  public Controller() {
  }
}
