package id.adrianz.ruangkelas.config;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class FirebaseConfigAdvice {

    private final FirebaseConfigProperties firebaseConfig;

    @ModelAttribute("firebaseConfig")
    public FirebaseConfigProperties firebaseConfig() {
        return firebaseConfig;
    }
}