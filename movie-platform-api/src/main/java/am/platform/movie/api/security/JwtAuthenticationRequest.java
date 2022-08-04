package am.platform.movie.api.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * @author mher13.02.94@gmail.com
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticationRequest {

    @NotNull
    @Email
    private String email;
    private String password;

}
