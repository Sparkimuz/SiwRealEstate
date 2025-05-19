package it.uniroma3.siw.authentication;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;
import static it.uniroma3.siw.model.Credentials.AGENT_ROLE;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {

    @Autowired
    private DataSource dataSource;

    /* ---------- JDBC authentication ---------- */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                PasswordEncoder passwordEncoder) throws Exception {
        auth.jdbcAuthentication()
            .dataSource(dataSource)
            .passwordEncoder(passwordEncoder)   // <-- usa lo stesso encoder dei servizi
            .authoritiesByUsernameQuery(
                "SELECT username, role FROM credentials WHERE username = ?")
            .usersByUsernameQuery(
                "SELECT username, password, true AS enabled FROM credentials WHERE username = ?");
    }

    /* ---------- Bean di supporto ---------- */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 12 è un buon compromesso tra sicurezza e prestazioni
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /* ---------- Configurazione HTTP ---------- */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            /* CSRF abilitato solo su form protetti – per le API/ricerche rest disabled */
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                /* ---- risorse pubbliche (GET) ---- */
                .requestMatchers(HttpMethod.GET,
                        "/", "/index", "/login", "/register",
                        "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico",
                        /* ricerca e listing pubblici */
                        "/property/**",             // lista e dettaglio immobili
                        "/agent/all",               // lista agenti
                        "/property/search",         // form ricerca proprietà
                        "/realEstateAgency/**"      // contatti / sede
                ).permitAll()

                /* ---- richieste pubbliche (POST) ---- */
                .requestMatchers(HttpMethod.POST,
                        "/login", "/register", "/property/search"
                ).permitAll()

                /* ---- aree riservate ---- */
                .requestMatchers("/admin/**").hasAuthority(ADMIN_ROLE)
                .requestMatchers("/agent/**").hasAuthority(AGENT_ROLE)
                /* qualunque altra rotta richiede login */
                .anyRequest().authenticated()
            )

            /* ---- form-login ---- */
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/success", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            /* ---- logout ---- */
            .logout(log -> log
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll()
            );

        return http.build();
    }
}
