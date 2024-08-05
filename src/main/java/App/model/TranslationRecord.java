package App.model;

import jakarta.persistence.*;

@Entity
@Table(name = "translation_record")
public class TranslationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_ip", nullable = false)
    private String userIp;

    @Column(name = "input_text", length = 100000, nullable = false)
    private String inputText;

    @Column(name = "translated_text", length = 100000, nullable = false)
    private String translatedText;


    public TranslationRecord() {
    }

    public TranslationRecord(String userIp, String inputText, String translatedText) {
        this.userIp = userIp;
        this.inputText = inputText;
        this.translatedText = translatedText;
    }


}
