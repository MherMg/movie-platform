package am.platform.movie.api.service;

import am.platform.movie.common.model.Category;
import am.platform.movie.common.model.Film;
import am.platform.movie.common.model.UserFilmHistory;
import am.platform.movie.common.repository.FilmRepository;
import am.platform.movie.common.repository.UserFilmHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mher13.02.94@gmail.com
 */

@Service
public class FilmService {

    private final FilmRepository filmRepository;
    private final UserFilmHistoryRepository userFilmHistoryRepository;
    private final UserService userService;

    @Autowired
    public FilmService(
            FilmRepository filmRepository,
            UserFilmHistoryRepository userFilmHistoryRepository,
            UserService userService
    ) {
        this.filmRepository = filmRepository;
        this.userFilmHistoryRepository = userFilmHistoryRepository;
        this.userService = userService;
    }

    public Film createFilm(String name, String description, int duration, LocalDate issueDate, Category category) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setDuration(duration);
        film.setIssueDate(issueDate);
        film.setCategory(category);
        return filmRepository.save(film);
    }

    public Optional<Film> findById(String filmId) {
        return filmRepository.findById(filmId);
    }


    public Page<Film> filter(String categoryId, LocalDate start, LocalDate end, int page, Integer size) {
        return filmRepository.filter(categoryId, start, end, page, size);
    }

    public void watchFilm(Film film) {
        UserFilmHistory userFilmHistory = new UserFilmHistory();
        userFilmHistory.setFilm(film);
        userFilmHistory.setUser(userService.getCurrentUser());
        userFilmHistoryRepository.save(userFilmHistory);
    }

    public Film suggestFilm() {
        List<UserFilmHistory> filmHistories = userFilmHistoryRepository.findAllByUser(userService.getCurrentUser());
        List<Category> categories = filmHistories
                .stream()
                .map(UserFilmHistory::getFilm)
                .map(Film::getCategory)
                .collect(Collectors.toList());

        Map<Category, Long> collect = categories.stream().collect(Collectors.groupingBy(category -> category, Collectors.counting()));

        Category category = Collections.max(collect.entrySet(), (entry1, entry2) -> Math.toIntExact(entry1.getValue() - entry2.getValue())).getKey();
        List<Film> filmsByCategory = filmRepository.findAllByCategory(category);
        if (filmsByCategory == null || filmsByCategory.isEmpty()) {
            return null;
        }
        int index = new Random().nextInt(filmsByCategory.size());
        return filmsByCategory.get(index);
    }
}
