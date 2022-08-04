package am.platform.movie.common.repository;


import am.platform.movie.common.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author mher13.02.94@gmail.com
 */

public interface UserRepository extends PagingAndSortingRepository<User, String> {

    boolean existsByEmailIgnoreCase(String email);

    User findByEmailIgnoreCase(String email);

    Optional<User> findOneByEmail(String email);

}
