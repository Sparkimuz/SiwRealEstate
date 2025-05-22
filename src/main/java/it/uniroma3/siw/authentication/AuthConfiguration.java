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
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
		.dataSource(dataSource)
		/*   Spring aggiunge automatic. “ROLE_” → in DB puoi salvare ADMIN / AGENT  */
		.authoritiesByUsernameQuery(
				"SELECT username, role FROM credentials WHERE username = ?")
		.usersByUsernameQuery(
				"SELECT username, password, true AS enabled FROM credentials WHERE username = ?");
	}

	/* ---------- Beans di supporto ---------- */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/* ---------- HTTP Security ---------- */
	@Bean
	protected SecurityFilterChain configure(final HttpSecurity http) throws Exception {

		http
		.csrf(csrf -> csrf.disable())     // disabilita davvero
		.cors(cors -> cors.disable())     // identico al progetto d’esempio
		.authorizeHttpRequests(auth -> auth
				/*  ---- Risorse pubbliche ----  */
				.requestMatchers(HttpMethod.GET,
						"/", "/index", "/login", "/register",
						"/css/**", "/images/**", "favicon.ico",
						"/property/**",                 // proprietà singole
						"/properties",
						"/realestateagencies/**",
						"/agents", "/agent/**",
						"/formSearchProperty",          //  ← ESATTO !
						"/formSearchProperty/**").permitAll()       //  ← con eventuale slash finale
				.requestMatchers(HttpMethod.POST,
						"/login", "/register", "/formSearchProperty").permitAll()

				/*  ---- Aree protette ----  */
				.requestMatchers("/admin/**").hasAnyAuthority(ADMIN_ROLE)
				.requestMatchers("/agent/**").hasAnyAuthority(AGENT_ROLE)

				/*  qualunque altra request → utente autenticato  */
				.anyRequest().authenticated())

		/*  ---- Login form ----  */
		.formLogin(form->form
				.loginPage("/login")
				.defaultSuccessUrl("/success", true)
				.failureUrl("/login?error=true")
				.permitAll())

		/*  ---- Logout ----  */
		.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID"));

		return http.build();
	}
}
