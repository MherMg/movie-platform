package am.platform.movie.api.security;

import am.platform.movie.common.model.User;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * @author mher13.02.94@gmail.com
 */

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    public CurrentUser(User user) {
        super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList("USER"));
    }

}
