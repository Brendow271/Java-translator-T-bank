package App.contoller;

import App.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/translate")
public class TranslationController {

    private final TranslationService translationService;

    @Autowired
    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping
    public String translate(@RequestBody Map<String, String> request, HttpServletRequest httpServletRequest) {
        String text = request.get("texts");
        String sourceLanguageCode = request.get("sourceLanguageCode");
        String targetLanguageCode = request.get("targetLanguageCode");
        String userIp = httpServletRequest.getRemoteAddr();
        return translationService.translateText(text, sourceLanguageCode, targetLanguageCode);//, userIp
    }
}