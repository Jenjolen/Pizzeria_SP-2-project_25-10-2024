package dat.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import dat.security.entities.User;

import java.io.Serializable;
import java.util.Set;

/**
 * Data Transfer Object for User
 * Purpose: To expose only necessary fields from the User entity
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PizzaUserDTO implements Serializable {

    private String username;
    private int age;
    private String email;
    private String gender;
    private Set<String> roles;

    /**
     * Constructor to map from User entity to PizzaUserDTO
     *
     * @param user the User entity
     */
    public PizzaUserDTO(User user) {
        this.username = user.getUsername();
        this.age = user.getAge();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.roles = user.getRolesAsStrings();
    }
}

