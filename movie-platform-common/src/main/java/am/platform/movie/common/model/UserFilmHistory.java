package am.platform.movie.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author mher13.02.94@gmail.com
 */

@Document("user_film_history")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserFilmHistory {

    @Id
    private String id;
    @DBRef
    private Film film;
    @DBRef
    private User user;
    private LocalDateTime created = LocalDateTime.now();

}
