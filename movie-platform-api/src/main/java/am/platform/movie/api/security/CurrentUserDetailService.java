package am.platform.movie.api.security;

import am.platform.movie.common.model.User;
import am.platform.movie.common.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author mher13.02.94@gmail.com
 */

@Service
public class CurrentUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CurrentUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findOneByEmail(s);

        user.orElseThrow(() -> new UsernameNotFoundException("User does not exist"));

        return new CurrentUser(user.get());
    }
}
