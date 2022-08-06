package am.platform.movie.api.service;

import am.platform.movie.common.model.Category;
import am.platform.movie.common.model.Film;
import am.platform.movie.common.repository.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author mher13.02.94@gmail.com
 */

@Service
public class FilmService {

    private final FilmRepository filmRepository;

    @Autowired
    public FilmService(
            FilmRepository filmRepository
    ) {
        this.filmRepository = filmRepository;
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
}
