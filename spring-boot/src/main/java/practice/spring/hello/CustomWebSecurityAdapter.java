package practice.spring.hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Dirt simple web security configuration with form login.
 */
@Configuration
public class CustomWebSecurityAdapter {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth ->
						auth.antMatchers("/", "/home").permitAll()
								.anyRequest().authenticated())
				.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
				.logout()
				.permitAll();
		return http.build();
	}

	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		// Disable security on endpoints where it is not needed
		return web -> web.ignoring().antMatchers("/user-cache");
	}

	@Bean
	UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(
				User.withDefaultPasswordEncoder()
						.username("user1")
						.password("password")
						.roles("USER")
						.build(),
				User.withDefaultPasswordEncoder()
						.username("user2")
						.password("password")
						.roles("USER")
						.build());
	}
}
