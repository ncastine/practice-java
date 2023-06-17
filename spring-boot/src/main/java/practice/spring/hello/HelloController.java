package practice.spring.hello;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloController {

    @RequestMapping("/user")
    public String user(HttpServletRequest request) {
        if (request.getUserPrincipal() != null) {
            return "Principal " + request.getUserPrincipal().getName();
        }

        return "No user";
    }
}
