package am.platform.movie.common.repository;

import am.platform.movie.common.model.Category;
import am.platform.movie.common.model.Film;
import am.platform.movie.common.repository.custom.FilmRepositoryCustom;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


/**
 * @author mher13.02.94@gmail.com
 */

public interface FilmRepository extends PagingAndSortingRepository<Film, String>, FilmRepositoryCustom {

    List<Film> findAllByCategory(Category category);


    @Query("{'name':{'$regex':'?0','$options':'i'}}")
    List<Film> searchByName(String name);
}
