package am.platform.movie.common.repository.custom;

import am.platform.movie.common.model.Film;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

/**
 * @author mher13.02.94@gmail.com
 */

public interface FilmRepositoryCustom {

    Page<Film> filter(String categoryId, LocalDate start, LocalDate end, int page, int size);

}
