package com.prescription.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.prescription.backend.exception.UnclearPrescriptionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GeminiAiService {
    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    @Value("${app.use-mock:true}")
    private boolean useMock;

    public GeminiAiService(WebClient.Builder webClientBuilder,
            @Value("${gemini.api-key}") String apiKey,
            ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
    }

    public Mono<String> analyzePrescription(String base64Image, String mimeType) {

        // ✅ MOCK MODE (default)
        if (useMock) {
            return Mono.just("Take rest, drink fluids, and do light stretching exercises.");
        }

        // ✅ REAL GEMINI CALL
        ObjectNode requestBody = buildRequestBody(base64Image, mimeType);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-2.0-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractResponse)
                .map(response -> {
                    if (response.contains("UNCLEAR_IMAGE")) {
                        throw new UnclearPrescriptionException("Prescription unclear. Please retake image.");
                    }
                    return response;
                });
    }

    private ObjectNode buildRequestBody(String base64Image, String mimeType) {
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode contents = root.putArray("contents");
        ObjectNode content = contents.addObject();
        ArrayNode parts = content.putArray("parts");

        // Text instruction
        ObjectNode textPart = parts.addObject();
        textPart.put("text",
                "Analyze this medical prescription image. Recommend relevant physical exercises or videos for the conditions found. If the image is blurry, unclear, or not a prescription, respond exactly with 'UNCLEAR_IMAGE' and nothing else.");

        // Image data
        ObjectNode inlineDataPart = parts.addObject();
        ObjectNode inlineData = inlineDataPart.putObject("inlineData");
        inlineData.put("mimeType", mimeType != null ? mimeType : "image/jpeg");
        inlineData.put("data", base64Image);

        return root;
    }

    private String extractResponse(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode candidates = rootNode.path("candidates");

            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");

                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }

            throw new RuntimeException("Invalid response from Gemini AI");

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

}
