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

@Document("category")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Category {

    @Id
    private String id;
    private String name;
    private LocalDateTime createdAt;
    @DBRef
    private Category parentCategory;
}
