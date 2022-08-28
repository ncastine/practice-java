package practice.spring.hello;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

@Configuration
public class ServletRequestDump {
	@Bean
	@RequestScope
	public Callable<String> personalMessage(HttpServletRequest request) {
		System.out.println("Request scope called " + request);
		return () -> "Test " + request.getRemoteUser();
	}
}
