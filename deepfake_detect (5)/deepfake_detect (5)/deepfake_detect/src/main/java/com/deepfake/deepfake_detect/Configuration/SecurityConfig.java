package com.deepfake.deepfake_detect.Configuration;

import com.deepfake.deepfake_detect.Service.JwtAuthenticationFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * BCrypt şifreleyici
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Ana SecurityFilterChain yapısı.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // CSRF korumasını devre dışı
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS ayarları

                // Yetkilendirme Kuralları
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .anyRequest().authenticated()
                )

                // Stateless oturum (JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Çift slash normalizasyon filtresi
                .addFilterBefore((Filter) (request, response, chain) -> {
                    HttpServletRequest httpRequest = (HttpServletRequest) request;
                    HttpServletResponse httpResponse = (HttpServletResponse) response;
                    String uri = httpRequest.getRequestURI();

                    if (uri.contains("//")) {
                        String normalizedUri = uri.replaceAll("//+", "/");
                        httpResponse.sendRedirect(normalizedUri);
                        return;
                    }
                    chain.doFilter(request, response);
                }, UsernamePasswordAuthenticationFilter.class)

                // JWT filtresini de ekle
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS yapılandırması: hangi origin, metod, header vs.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Tüm path'lere bu kuralları uygula
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * HttpFirewall bean'i: özel karakter izinleri, çift slash, %2F vs.
     * Ancak Spring Security 6'da aktif etmek için "webSecurityCustomizer(...)"
     * veya benzeri config gerekir. Yine de bean olarak tutuyoruz.
     */
    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);   // "%2F"
        firewall.setAllowBackSlash(true);
        firewall.setAllowUrlEncodedPercent(true); // "%25"
        firewall.setAllowSemicolon(true);         // ";"
        firewall.setAllowUrlEncodedPeriod(true);  // "."
        return firewall;
    }

    /**
     * AuthenticationManager (login vb. işlemler için)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
