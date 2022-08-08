package am.platform.movie.api.rest;

import am.platform.movie.api.rest.dto.FilmDto;
import am.platform.movie.api.rest.response.ResponseInfo;
import am.platform.movie.api.service.CategoryService;
import am.platform.movie.api.service.FilmService;
import am.platform.movie.common.model.Category;
import am.platform.movie.common.model.Film;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static am.platform.movie.api.rest.response.ResponseMessage.*;

/**
 * @author mher13.02.94@gmail.com
 */

@RestController
@CrossOrigin
@RequestMapping("/api/private/v1/film")
public class FilmController {
    private final CategoryService categoryService;
    private final FilmService filmService;

    @Autowired
    public FilmController(
            CategoryService categoryService,
            FilmService filmService
    ) {
        this.categoryService = categoryService;
        this.filmService = filmService;
    }

    public static class FilmRequest {
        @NotBlank
        public String name;
        @NotBlank
        public String description;
        @NotBlank
        @JsonFormat(pattern = "yyyy-MM-dd")
        public LocalDate issueDate;
        @NotBlank
        public int duration;
        @NotBlank
        public String categoryId;
    }


    @AllArgsConstructor
    public static class FilmListResponse {
        public List<FilmDto> films;
        public int totalPages;
        public long totalElements;
    }


    @ApiResponses({
            @ApiResponse(code = 404, message = "CATEGORY_NOT_FOUND", response = ResponseInfo.class),
            @ApiResponse(code = 201, message = "", response = FilmDto.class)
    })
    @ApiOperation(value = "API for add film. Has authority - ADMIN ", authorizations = {@Authorization(value = "Bearer")})
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping()
    public HttpEntity<?> createFilm(@RequestBody FilmRequest request) {

        Optional<Category> categoryOptional = categoryService.findCategoryById(request.categoryId);
        if (categoryOptional.isEmpty()) {
            return ResponseEntity.status(404).body(ResponseInfo.createResponse(CATEGORY_NOT_FOUND));
        }

        Film film = filmService.createFilm(request.name, request.description, request.duration, request.issueDate, categoryOptional.get());

        return ResponseEntity.status(201).body(new FilmDto(film));
    }


    @ApiResponses({
            @ApiResponse(code = 404, message = "CATEGORY_NOT_FOUND", response = ResponseInfo.class),
            @ApiResponse(code = 200, message = "", response = FilmDto.class)
    })
    @ApiOperation(value = "API for get film by id", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("/{filmId}")
    public HttpEntity<?> getFilmById(@PathVariable String filmId) {

        Optional<Film> filmOptional = filmService.findById(filmId);
        if (filmOptional.isEmpty()) {
            return ResponseEntity.status(404).body(ResponseInfo.createResponse(FILM_NOT_FOUND));
        }
        return ResponseEntity.status(200).body(new FilmDto(filmOptional.get()));
    }

    @ApiResponses({
            @ApiResponse(code = 404, message = "CATEGORY_NOT_FOUND", response = ResponseInfo.class),
            @ApiResponse(code = 400, message = "INCORRECT_BETWEEN_YEAR", response = ResponseInfo.class),
            @ApiResponse(code = 200, message = "", response = FilmDto.class)
    })
    @ApiOperation(value = "API for filtering film", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("/filter")
    public HttpEntity<?> filter(
            @RequestParam(required = false) String categoryId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size", required = false) Integer size
    ) {

        if (
                start != null && end == null
                        || start == null && end != null
        ) {
            return ResponseEntity.status(404).body(ResponseInfo.createResponse(INCORRECT_BETWEEN_YEAR));

        }
        if (categoryId != null) {
            Optional<Category> categoryOptional = categoryService.findCategoryById(categoryId);
            if (categoryOptional.isEmpty()) {
                return ResponseEntity.status(404).body(ResponseInfo.createResponse(CATEGORY_NOT_FOUND));
            }
        }

        Page<Film> films = filmService.filter(categoryId, start, end, page, size);
        return ResponseEntity.status(200).body(
                new FilmListResponse(films.getContent().stream().map(FilmDto::new).collect(Collectors.toList()), films.getTotalPages(), films.getTotalElements())
        );
    }


    @ApiResponses({
            @ApiResponse(code = 404, message = "CATEGORY_NOT_FOUND", response = ResponseInfo.class),
            @ApiResponse(code = 200, message = "", response = FilmDto.class)
    })
    @ApiOperation(value = "API for search film by name", authorizations = {@Authorization(value = "Bearer")})
    @GetMapping("/search/{name}")
    public HttpEntity<?> searchFilmByName(@PathVariable String name) {

        List<Film> films = filmService.searchByFuzzyName(name);
        if (films.isEmpty()) {
            return ResponseEntity.status(404).body(ResponseInfo.createResponse(FILM_NOT_FOUND));
        }
        return ResponseEntity.status(200).body(films.stream().map(FilmDto::new).collect(Collectors.toList()));
    }
}
