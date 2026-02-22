package com.example.main;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/fact-check")
public class FactCheckerController {

    private final EvidenceService evidenceClient;
    private final MistralClient mistralService;
    private final ReportService reportservice;

    public FactCheckerController(EvidenceService evidenceClient, MistralClient mistralService,ReportService reportservice) {
        this.evidenceClient = evidenceClient;
        this.mistralService = mistralService;
        this.reportservice = reportservice;
    }

    @GetMapping
    public ResponseEntity<byte[]> factCheck(@RequestParam String query) throws Exception {
        // 1. Get evidence from Evidence Service
        var evidenceResponse = evidenceClient.getEvidence(query);

        // Extract relevant evidence text
        String evidenceText = evidenceResponse.toString(); // You can refine this parsing

        // 2. Pass to Mistral for AI fact-check
        System.out.println(evidenceResponse);

        
        ObjectMapper mapper = new ObjectMapper();
        String s = mistralService.checkFact(query, evidenceText);
        FactCheckResult result = mapper.readValue(s, FactCheckResult.class);
        ArrayList<FactCheckResult> list = new ArrayList<FactCheckResult>();
        list.add(result);
        System.out.println(list.get(0).toString());
        return reportservice.generatePdf(list);
        
    }
}
