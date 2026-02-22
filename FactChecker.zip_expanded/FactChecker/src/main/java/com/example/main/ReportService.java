package com.example.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "report-service", url = "http://localhost:8082")
public interface ReportService {

	  @PostMapping(value = "/reports/pdf", consumes = "application/json")
	  public ResponseEntity<byte[]> generatePdf(@RequestBody ArrayList<FactCheckResult> results) ;
}
