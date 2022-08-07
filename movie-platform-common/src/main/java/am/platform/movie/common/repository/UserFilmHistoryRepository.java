package am.platform.movie.common.repository;

import am.platform.movie.common.model.User;
import am.platform.movie.common.model.UserFilmHistory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author mher13.02.94@gmail.com
 */

public interface UserFilmHistoryRepository extends PagingAndSortingRepository<UserFilmHistory, String> {

    List<UserFilmHistory> findAllByUser(User user);
}
