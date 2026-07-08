package id.adrianz.ruangkelas.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount =
                getClass().getClassLoader().getResourceAsStream("secrets/firebase-service-account.json");

	    if (serviceAccount == null) {
                try {
                    serviceAccount = new FileInputStream("/app/secrets/firebase-service-account.json");
                } catch (IOException e) {
                    serviceAccount = null;
                }
            }

            if (serviceAccount == null) {
                log.warn("⚠️ Firebase service account not found, skipping initialization.");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            FirebaseApp.initializeApp(options);
            log.info("✅ Firebase berhasil diinisialisasi");
        }
    }
}
