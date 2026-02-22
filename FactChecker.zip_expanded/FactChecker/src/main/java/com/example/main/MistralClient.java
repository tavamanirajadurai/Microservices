package com.example.main;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class MistralClient {

    
    private String apiKey="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";

    private String mistralUrl="https://openrouter.ai/api/v1/chat/completions";

    private final ObjectMapper mapper = new ObjectMapper();

    public String checkFact(String query, String evidence) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(mistralUrl);
            post.addHeader("Authorization", "Bearer " + apiKey);
            post.addHeader("Content-Type", "application/json");

            Map<String, Object> body = Map.of(
                "model", "mistralai/mixtral-8x7b-instruct",
                "messages", List.of(
                    Map.of("role", "system", "content", "You are a fact-checking assistant."),
                    Map.of("role", "user", "content", 
                           "Claim: " + query + "\nEvidence: " + evidence + "\nVerify if the claim is true or false with reasoning.")
                )
            );

            post.setEntity(new StringEntity(mapper.writeValueAsString(body)));

            var response = client.execute(post);
            var json = mapper.readTree(response.getEntity().getContent());
            return json.toString();
        }
    }
}