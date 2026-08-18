package com.aydog4nn.manitimleproje.controller;

import com.aydog4nn.manitimleproje.dto.movie.MovieSearchResponse;
import com.aydog4nn.manitimleproje.service.abs.MovieCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/movies")
@Tag(name = "Film Arama", description = "TMDB üzerinden film araması yapar. TMDB anahtarı sadece backend tarafında tutulur, tarayıcıya gönderilmez.")
@SecurityRequirement(name = "bearerAuth")
public class MovieCatalogController {
    private final MovieCatalogService movieCatalogService;
    public MovieCatalogController(MovieCatalogService movieCatalogService) { this.movieCatalogService = movieCatalogService; }

    @GetMapping("/search")
    @Operation(summary = "Film ara", description = "En az iki karakter girerek TMDB film araması yapar. Sonuçlar ortak listeye eklenmek için kullanılır.")
    public List<MovieSearchResponse> search(@RequestParam("query") @Size(min = 2, max = 100) String query) { return movieCatalogService.search(query.trim()); }
}
