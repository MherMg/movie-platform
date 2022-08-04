package am.platform.movie.common.repository;

import am.platform.movie.common.model.EmailVerify;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author mher13.02.94@gmail.com
 */

public interface EmailVerifyRepository extends PagingAndSortingRepository<EmailVerify, String> {

    EmailVerify findByEmail(String email);
}
