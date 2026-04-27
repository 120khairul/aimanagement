package com.example.academicfeedback.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class OpenAiClientService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiKey;
    private final String model;
    private final String url;

    public OpenAiClientService(ObjectMapper objectMapper,
                               @Value("${openai.api-key:}") String apiKey,
                               @Value("${openai.model:gpt-5.4}") String model,
                               @Value("${openai.url:https://api.openai.com/v1/responses}") String url) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.url = url;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String getModel() {
        return model;
    }

    public String generateText(String task, String context, String fallbackText) {
        if (!isConfigured()) {
            return fallbackText + "\n\nNote: OPENAI_API_KEY is not set, so this fallback draft was generated locally.";
        }

        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("model", model);
            body.put("instructions", "You are an academic quality assurance assistant. Write concise, practical, teacher-friendly output. Do not invent marks, names, dates, or facts that are not in the context.");
            body.put("input", task + "\n\nContext:\n" + context);
            body.put("max_output_tokens", 700);

            String payload = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(45))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return fallbackText + "\n\nOpenAI request failed with status " + response.statusCode() + ". Local fallback was used.";
            }

            String text = extractText(response.body());
            if (text.isBlank()) {
                return fallbackText + "\n\nOpenAI returned an empty response. Local fallback was used.";
            }
            return text.trim();
        } catch (IOException exception) {
            return fallbackText + "\n\nOpenAI response could not be parsed. Local fallback was used.";
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return fallbackText + "\n\nOpenAI request was interrupted. Local fallback was used.";
        } catch (RuntimeException exception) {
            return fallbackText + "\n\nOpenAI request could not be completed. Local fallback was used.";
        }
    }

    private String extractText(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode outputText = root.path("output_text");
        if (outputText.isTextual()) {
            return outputText.asText();
        }

        StringBuilder builder = new StringBuilder();
        JsonNode output = root.path("output");
        if (output.isArray()) {
            for (JsonNode outputItem : output) {
                JsonNode content = outputItem.path("content");
                if (content.isArray()) {
                    for (JsonNode contentItem : content) {
                        JsonNode text = contentItem.path("text");
                        if (text.isTextual()) {
                            builder.append(text.asText()).append("\n");
                        }
                    }
                }
            }
        }
        return builder.toString();
    }
}
