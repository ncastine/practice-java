package practice.spring.hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

/**
 * Dirt simple web security configuration with form login.
 */
@Configuration
public class SecurityConfiguration {

    /**
     * Name of users file to load on startup.
     */
    private static final String USERS_FILE = "users.properties";

    /**
     * Prioritized list of paths to search for users file to load.
     */
    private static final List<String> USERS_FILE_PATHS = List.of(
            System.getProperty("user.home") + "/.config/spring-playground/"
                    + USERS_FILE, "target/" + USERS_FILE, USERS_FILE);

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
        // NOT suitable for production
        return new FileLoadUserDetailsService(USERS_FILE_PATHS);
    }
}
