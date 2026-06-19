package id.adrianz.ruangkelas.config;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import id.adrianz.ruangkelas.service.UserService;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/api/jadwal/**",
                    "/api/notification/firebase-messaging-sw.js"
                ).permitAll()
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/auth/login")
                .defaultSuccessUrl("/", true)
                .failureHandler((request, response, exception) -> {
                    String message;
                    if (exception instanceof BadCredentialsException) {
                        message = "Email/username atau password salah";
                    } else if (exception instanceof DisabledException) {
                        message = "Akun belum aktif";
                    } else if (exception instanceof LockedException) {
                        message = "Akun terkunci";
                    } else {
                        message = "Login gagal, coba lagi";
                    }
                    response.sendRedirect("/auth/login?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
                })
                .permitAll()
                )
                .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .deleteCookies("APP_SESSION")
                .invalidateHttpSession(true)
                .permitAll()
                )
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .expiredUrl("/auth/login?expired=true")
                )
                .rememberMe(remember -> remember
                .userDetailsService(userService)
                .tokenValiditySeconds(86400)
                .rememberMeCookieName("REMEMBER_ME")
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

}
