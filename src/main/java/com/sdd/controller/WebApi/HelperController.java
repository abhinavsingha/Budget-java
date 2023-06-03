package com.sdd.controller.WebApi;

import com.sdd.entities.CdaParking;
import com.sdd.response.ApiResponse;
import com.sdd.service.HelperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/helperController")
@Slf4j
public class HelperController {

  @Autowired     private HelperService helperService;

  @GetMapping("/generatePDFDemo")
  public String generatePDFDemo() {
    return helperService.generatePDFDemo();
  }
}
