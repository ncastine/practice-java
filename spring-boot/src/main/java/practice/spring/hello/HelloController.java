package practice.spring.hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

@RestController
public class HelloController {
    @Autowired
    private Callable<String> personalMessage;

    @RequestMapping("/")
    public String index() throws Exception {
        final String message = personalMessage.call();
        return "Greetings from Spring Boot!" + (message == null ? "" : " " + message);
    }

    @RequestMapping("/user")
    public String user(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "Principal " + request.getUserPrincipal().getName();
        }

        return "No user";
    }
}
