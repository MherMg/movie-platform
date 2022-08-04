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

@Document("email_verify")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmailVerify {

    @Id
    private String id;
    private String email;
    private String code;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private State state = State.SENT;

    public enum State {
        SENT,
        PROCESSED
    }
}
