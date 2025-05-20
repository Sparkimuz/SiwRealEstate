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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
    public static PasswordEncoder passwordEncoder() {
        // 12 è un buon compromesso tra sicurezza e prestazioni
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /* ---------- Configurazione HTTP ---------- */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            /* Niente CSRF (facoltativo, ma di solito lo disattivi se non usi form protetti) */
            .csrf(AbstractHttpConfigurer::disable)

            /*  PERMETTE TUTTO  */
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            /* Disabilita login form, http-basic e qualunque meccanismo di auth */
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
