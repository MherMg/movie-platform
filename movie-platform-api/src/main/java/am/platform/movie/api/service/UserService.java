package am.platform.movie.api.service;

import am.platform.movie.common.model.User;
import am.platform.movie.common.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author mher13.02.94@gmail.com
 */

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(String email, String password) {

        User user = new User();
        user.setEmail(email);
        user.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        log.debug("User registered: [id:{};email:{}]", user.getId(), user.getEmail());

    }

    public boolean userExists(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Nullable
    public User loadUserByEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email);

        if (user == null) {
            log.warn("User [email:{}] is not found", email);
            return null;
        }
        return user;
    }

    public void userVerified(User user) {
        user.setState(User.UserState.ACTIVE);
        user.setStateAt(LocalDateTime.now(ZoneOffset.UTC));
        userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        if (currentUser == null) {
            throw new AccessDeniedException("Current user is not found");
        }
        String email = currentUser.getName();

        User user = loadUserByEmail(email);

        if (user == null) {
            log.info("current user not found");
        }
        return user;
    }

    public User updateUser(User user, String name) {
        user.setName(name);
        return userRepository.save(user);
    }

    public void changePassword(String newPassword, User user) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(User user) {
        user.setStateAt(LocalDateTime.now(ZoneOffset.UTC));
        user.setState(User.UserState.DELETED);
        userRepository.save(user);
    }

    public User updateEmail(User user, String email) {
        user.setEmail(email);
        return userRepository.save(user);
    }

}
