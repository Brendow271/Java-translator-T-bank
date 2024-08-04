package App.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.io.IOException;

@Service
public class TranslationService {
    private final RestTemplate restTemplate;
    private final String yandexApiKey;
    private final String idFolder;
    private final ExecutorService executorService;
    private final ObjectMapper objectMapper;
    @Autowired
    public TranslationService(RestTemplate restTemplate) {
        this.yandexApiKey = "t1.9euelZrOmJTHxsaQzcuUx8iMm4yLje3rnpWanseNl5LNz5zKkZyWnZidkczl9fdkQEJK-e9JGsTd9fckbz9K-e9JGsTN5_XrnpWaz5yayJGNzMfIl4-TjJvGzpXv_MXrnpWaz5yayJGNzMfIl4-TjJvGzpU.sC57OEUuuCUp86Alvn5yfg_eUkWzSo2zZRbVKXdL0T1MjxW28FOJvFrjRpGuUSAzNvNLsnjIpySb_lKkMKsGBQ";
        this.restTemplate = restTemplate;
        this.idFolder = "b1g9l2de1r3n3ivlesju";
        this.executorService = Executors.newFixedThreadPool(10);
        this.objectMapper = new ObjectMapper();
    }

    public String translateText(String text, String sourceLanguageCode, String targetLanguageCode) {//, String userIp

        List<Future<String>> futures = List.of(text.split("\\s+")).stream()
                .map(word -> executorService.submit(() -> translateWord(word, sourceLanguageCode, targetLanguageCode)))
                .collect(Collectors.toList());

        StringBuilder translatedText = new StringBuilder();
        for (Future<String> future : futures) {
            try {
                translatedText.append(future.get()).append(" ");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        String result = translatedText.toString().trim();
        return result;

    }

    private String translateWord(String texts, String sourceLanguageCode, String targetLanguageCode) {
        String apiUrl = "https://translate.api.cloud.yandex.net/translate/v2/translate";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + yandexApiKey);

        Map<String, String> body = Map.of("folderId", idFolder,
                        "texts", texts,
                        "targetLanguageCode", targetLanguageCode,
                        "sourceLanguageCode",sourceLanguageCode);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
        return parseTranslationResponse(response.getBody());
    }

    private String parseTranslationResponse(String response) {

        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode translationsNode = root.path("translations");
            if (translationsNode.isArray() && translationsNode.size() > 0) {
                return translationsNode.get(0).path("text").asText();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
