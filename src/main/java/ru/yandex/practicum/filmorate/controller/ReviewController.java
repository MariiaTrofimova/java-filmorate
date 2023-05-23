package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService service;

    @Autowired
    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Review findReviewById(@PathVariable long id) {
        return service.findReviewById(id);
    }

    @GetMapping("filmId={filmId}&count={count}")
    public List<Review> findReviewsByFilmId(@RequestParam(defaultValue = "-1") long filmId, @RequestParam(defaultValue = "10") Integer
            count) {                                               //Получение всех отзывов по идентификатору фильма,
        return service.findReviewsByFilmId(filmId,  count); //если фильм не указан то все. Если кол-во не указано то 10.
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@Valid @RequestBody Review review) {
        return service.addReview(review);
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Review updateReview(@Valid @RequestBody Review review) {
        return service.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public boolean deleteReviewById(@PathVariable long id) {
        return service.deleteReviewById(id);
    }

    //лайки отзывам
    @PutMapping("{id}/like/{userId}") // пользователь ставит лайк отзыву.
    public List<Long> addLikeReview(@PathVariable long id, @PathVariable long userId) {
        return service.addLikeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}") //пользователь ставит дизлайк отзыву.
    public List<Long> addDislikeReview(@PathVariable long id, @PathVariable long userId) {
        return service.addDislikeReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}") //пользователь удаляет лайк отзыву.
    public List<Long> deleteLikeReview(@PathVariable long id, @PathVariable long userId) {
        return service.deleteLikeReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}") //пользователь удаляет дизлайк отзыву.
    public List<Long> deleteDislikeReview(@PathVariable long id, @PathVariable long userId) {
        return service.deleteDislikeReview(id, userId);
    }
}
