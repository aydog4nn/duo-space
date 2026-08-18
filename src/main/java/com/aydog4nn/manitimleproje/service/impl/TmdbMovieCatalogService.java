package com.aydog4nn.manitimleproje.service.impl;

import com.aydog4nn.manitimleproje.config.TmdbProperties;
import com.aydog4nn.manitimleproje.dto.movie.MovieSearchResponse;
import com.aydog4nn.manitimleproje.exception.MovieApiException;
import com.aydog4nn.manitimleproje.service.abs.MovieCatalogService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.util.List;

@Service
public class TmdbMovieCatalogService implements MovieCatalogService {
    private final RestClient restClient;
    private final TmdbProperties properties;

    public TmdbMovieCatalogService(TmdbProperties properties) {
        this.restClient = RestClient.builder().baseUrl(properties.baseUrl()).build();
        this.properties = properties;
    }

    @Override
    public List<MovieSearchResponse> search(String query) {
        if (!StringUtils.hasText(properties.apiReadAccessToken())) throw new MovieApiException("Film araması için TMDB anahtarı henüz ayarlanmadı.");
        try {
            TmdbSearchResult result = restClient.get().uri(builder -> builder.path("/search/movie").queryParam("query", query).queryParam("language", properties.language()).queryParam("include_adult", false).build()).header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.apiReadAccessToken()).retrieve().body(TmdbSearchResult.class);
            return result == null || result.results() == null ? List.of() : result.results().stream().limit(10).map(this::toResponse).toList();
        } catch (RestClientException exception) {
            throw new MovieApiException("Film servisine şu an ulaşılamıyor. Biraz sonra tekrar dene.");
        }
    }

    private MovieSearchResponse toResponse(TmdbMovie movie) {
        String year = StringUtils.hasText(movie.releaseDate()) && movie.releaseDate().length() >= 4 ? movie.releaseDate().substring(0, 4) : null;
        String posterUrl = StringUtils.hasText(movie.posterPath()) ? properties.imageBaseUrl() + movie.posterPath() : null;
        return new MovieSearchResponse(movie.id(), movie.title(), year, posterUrl, movie.overview(), movie.voteAverage());
    }

    private record TmdbSearchResult(List<TmdbMovie> results) { }
    private record TmdbMovie(Long id, String title, String overview, @JsonProperty("vote_average") Double voteAverage, @JsonProperty("poster_path") String posterPath, @JsonProperty("release_date") String releaseDate) { }
}
