package App.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class TranslationService {


    @Autowired
    public TranslationService() {

    }

    public String translateText(String text, String sourceLang, String targetLang, String userIp) {

        return text + sourceLang + targetLang + userIp;
    }





}
