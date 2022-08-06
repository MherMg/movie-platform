package am.platform.movie.common.repository;

import am.platform.movie.common.model.Film;
import am.platform.movie.common.repository.custom.FilmRepositoryCustom;
import org.springframework.data.repository.PagingAndSortingRepository;


/**
 * @author mher13.02.94@gmail.com
 */

public interface FilmRepository extends PagingAndSortingRepository<Film, String>, FilmRepositoryCustom {

}
