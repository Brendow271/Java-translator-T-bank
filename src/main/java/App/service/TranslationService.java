package App.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TranslationService {
    private final RestTemplate restTemplate;
    private final String yandexApiKey;
    private final String idFolder;
    @Autowired
    public TranslationService(RestTemplate restTemplate) {
        this.yandexApiKey = "t1.9euelZrOmJTHxsaQzcuUx8iMm4yLje3rnpWanseNl5LNz5zKkZyWnZidkczl9fdkQEJK-e9JGsTd9fckbz9K-e9JGsTN5_XrnpWaz5yayJGNzMfIl4-TjJvGzpXv_MXrnpWaz5yayJGNzMfIl4-TjJvGzpU.sC57OEUuuCUp86Alvn5yfg_eUkWzSo2zZRbVKXdL0T1MjxW28FOJvFrjRpGuUSAzNvNLsnjIpySb_lKkMKsGBQ";
        this.restTemplate = restTemplate;
        this.idFolder = "b1g9l2de1r3n3ivlesju";
    }

    public String translateText(String text, String sourceLanguageCode, String targetLanguageCode) {//, String userIp

        String ans = translateWord(text, sourceLanguageCode, targetLanguageCode);
        return ans;
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
        return response;
    }


}
