package id.adrianz.ruangkelas.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    private final ResourceLoader resourceLoader;

    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // 1. Coba cari di dalam classpath (src/main/resources/secrets/...)
                Resource resource = resourceLoader.getResource("classpath:secrets/firebase-service-account.json");
                
                // 2. Jika tidak ada di classpath, otomatis cari file fisik di root project
                if (!resource.exists()) {
                    resource = resourceLoader.getResource("file:src/main/resources/secrets/firebase-service-account.json");
                }
                
                if (!resource.exists()) {
                    throw new IOException("File firebase-service-account.json tidak ditemukan di classpath maupun file sistem lokal!");
                }

                try (InputStream serviceAccount = resource.getInputStream()) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();

                    FirebaseApp.initializeApp(options);
                }
            }
        } catch (IOException e) {
            System.err.println("Gagal menginisialisasi Firebase: " + e.getMessage());
            // Agar aplikasi tidak crash saat startup jika Firebase opsional, 
            // Anda bisa me-log errornya saja alih-alih melempar runtime exception.
            throw new RuntimeException(e); 
        }
    }
}