package am.platform.movie.api.rest.dto;

import am.platform.movie.common.model.Film;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author mher13.02.94@gmail.com
 */

@Data
public class FilmDto {
    public String id;
    public String name;
    public String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate issueDate;
    public int duration;
    public CategoryDto category;


    public FilmDto(Film film) {
        this.id = film.getId();
        this.name = film.getName();
        this.description = film.getDescription();
        this.issueDate = film.getIssueDate();
        this.duration = film.getDuration();
        this.category = new CategoryDto(film.getCategory());

    }


}
