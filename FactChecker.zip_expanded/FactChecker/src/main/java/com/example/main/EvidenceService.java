package com.example.main;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "evidence-service", url = "http://localhost:8081")
public interface EvidenceService {

	  @GetMapping("/evidence/getevidence")
	    Map<String, Object> getEvidence(@RequestParam("claim") String query);
}
