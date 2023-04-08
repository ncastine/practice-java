package practice.spring.hello;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DO NOT use this for production. Abbreviated user details for load or save
 * to JSON file. The password is NOT hashed.
 */
public class NonHashedUserDetails {
    private final String name;
    private final String password;
    private final List<String> roles;

    /**
     * Construct abbreviated user details.
     */
    @JsonCreator
    public NonHashedUserDetails(
            @JsonProperty("name") String name,
            @JsonProperty("password") String password,
            @JsonProperty("roles") List<String> roles) {
        this.name = name;
        this.password = password;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoles() {
        return roles;
    }
}
