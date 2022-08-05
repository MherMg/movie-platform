package am.platform.movie.common.repository;

import am.platform.movie.common.model.Category;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author mher13.02.94@gmail.com
 */

public interface CategoryRepository extends PagingAndSortingRepository<Category, String> {


    List<Category> findAllByParentCategory(Category category);

}
