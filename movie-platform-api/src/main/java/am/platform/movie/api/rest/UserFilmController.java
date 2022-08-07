package am.platform.movie.api.rest;

import am.platform.movie.api.rest.dto.FilmDto;
import am.platform.movie.api.rest.response.ResponseInfo;
import am.platform.movie.api.service.FilmService;
import am.platform.movie.common.model.Film;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static am.platform.movie.api.rest.response.ResponseMessage.FILM_NOT_FOUND;

/**
 * @author mher13.02.94@gmail.com
 */

@RestController
@CrossOrigin
@RequestMapping("/api/private/v1/user/film")
public class UserFilmController {
    private final FilmService filmService;

    @Autowired
    public UserFilmController(
            FilmService filmService
    ) {
        this.filmService = filmService;
    }


    @ApiResponses({
            @ApiResponse(code = 404, message = "FILM_NOT_FOUND", response = ResponseInfo.class),
            @ApiResponse(code = 20, message = "")
    })
    @ApiOperation(value = "API for watch film", authorizations = {@Authorization(value = "Bearer")})
    @PostMapping("/watch/{filmId}")
    public HttpEntity<?> createFilm(@PathVariable String filmId) {

        Optional<Film> filmOptional = filmService.findById(filmId);
        if (filmOptional.isEmpty()) {
            return ResponseEntity.status(404).body(ResponseInfo.createResponse(FILM_NOT_FOUND));
        }

        filmService.watchFilm(filmOptional.get());

        return ResponseEntity.ok().build();
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "", response = FilmDto.class)
    })
    @ApiOperation(value = "API for suggestion film to user", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("/suggest")
    public HttpEntity<?> suggestFilm() {
        Film film = filmService.suggestFilm();
        if (film == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.status(200).body(new FilmDto(film));
    }


}
