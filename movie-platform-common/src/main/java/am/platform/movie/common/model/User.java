package am.platform.movie.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author mher13.02.94@gmail.com
 */

@Document("users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    private String id;
    private String email;
    private String name;
    private LocalDateTime createdAt;
    private String password;
    private UserState state;
    private LocalDateTime stateAt;

    public enum UserState {
        ACTIVE, DELETED
    }

}
