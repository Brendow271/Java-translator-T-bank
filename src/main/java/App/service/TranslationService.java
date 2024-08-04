package App.service;

import App.exception.TranslationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.io.IOException;

@Service
public class TranslationService {
    private final RestTemplate restTemplate;
    private final String yandexApiKey;
    private final String idFolder;
    private final ExecutorService executorService;
    private final ObjectMapper objectMapper;
    private final Semaphore semaphore;
    private final ScheduledExecutorService scheduler;

    @Autowired
    public TranslationService(RestTemplate restTemplate) {
        this.yandexApiKey = "t1.9euelZrOmJTHxsaQzcuUx8iMm4yLje3rnpWanseNl5LNz5zKkZyWnZidkczl9fdkQEJK-e9JGsTd9fckbz9K-e9JGsTN5_XrnpWaz5yayJGNzMfIl4-TjJvGzpXv_MXrnpWaz5yayJGNzMfIl4-TjJvGzpU.sC57OEUuuCUp86Alvn5yfg_eUkWzSo2zZRbVKXdL0T1MjxW28FOJvFrjRpGuUSAzNvNLsnjIpySb_lKkMKsGBQ";
        this.restTemplate = restTemplate;
        this.idFolder = "b1g9l2de1r3n3ivlesju";
        this.executorService = Executors.newFixedThreadPool(10);
        this.objectMapper = new ObjectMapper();
        this.semaphore = new Semaphore(20);
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public String translateText(String text, String sourceLanguageCode, String targetLanguageCode) {//, String userIp

        List<Future<String>> futures = List.of(text.split("\\s+")).stream()
                .map(word -> executorService.submit(() -> {
                    try {
                        semaphore.acquire();
                        return translateWord(word, sourceLanguageCode, targetLanguageCode);
                    } finally {
                        scheduler.schedule(() -> semaphore.release(), 1, TimeUnit.SECONDS);
                    }
                }))
                .collect(Collectors.toList());


        StringBuilder translatedText = new StringBuilder();

        for (Future<String> future : futures) {
            try {
                translatedText.append(future.get()).append(" ");
            } catch (InterruptedException | ExecutionException e) {
                if (e.getCause() instanceof TranslationException) {
                    throw (TranslationException) e.getCause();
                } else {
                    e.printStackTrace();
                    translatedText.append("Error ");
                }
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

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {

                return parseTranslationResponse(response.getBody());
            } else {
                throw new TranslationException("Error: Received non-200 response code.", (HttpStatus) response.getStatusCode());
            }
        } catch (HttpClientErrorException.BadRequest e) {
            throw new TranslationException("Error: Invalid language code or bad request.", HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new TranslationException("Error: Unauthorized. Check your API key.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.out.println(e);
            throw new TranslationException("Error: An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
//        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
//        return parseTranslationResponse(response.getBody());
    }

    private String parseTranslationResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode translationsNode = root.path("translations");
            if (translationsNode.isArray() && translationsNode.size() > 0) {
                return translationsNode.get(0).path("text").asText();
            } else {
                throw new TranslationException("Error: No translations found in the response.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new TranslationException("Error: Failed to parse the translation response.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
