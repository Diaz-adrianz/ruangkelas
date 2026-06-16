package id.adrianz.ruangkelas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RuangkelasApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuangkelasApplication.class, args);
    }
}