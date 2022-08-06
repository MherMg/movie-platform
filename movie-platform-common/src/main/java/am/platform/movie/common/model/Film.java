package am.platform.movie.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

/**
 * @author mher13.02.94@gmail.com
 */

@Document("film")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Film {

    @Id
    private String id;
    private String name;
    private String description;
    //minute
    private int duration;
    private LocalDate issueDate;
    @DBRef
    private Category category;
    private LocalDateTime createdAt = LocalDateTime.now();

}
