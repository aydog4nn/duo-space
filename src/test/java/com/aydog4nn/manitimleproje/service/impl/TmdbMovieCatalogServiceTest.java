package com.aydog4nn.manitimleproje.service.impl;

import com.aydog4nn.manitimleproje.config.TmdbProperties;
import com.aydog4nn.manitimleproje.dto.movie.MovieSearchResponse;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TmdbMovieCatalogServiceTest {

    @Test
    void shouldConvertTmdbResponseToMovieSearchResponse() throws Exception {
        HttpServer fakeTmdbServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        fakeTmdbServer.createContext("/search/movie", exchange -> {
            String response = """
                    {"results":[{"id":122,"title":"Yüzüklerin Efendisi","overview":"Orta Dünya macerası","vote_average":8.4,"poster_path":"/poster.jpg","release_date":"2001-12-19"}]}
                    """;
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            }
        });
        fakeTmdbServer.start();

        try {
            int port = fakeTmdbServer.getAddress().getPort();
            TmdbProperties properties = new TmdbProperties(
                    "test-token",
                    "http://127.0.0.1:" + port,
                    "https://image.tmdb.org/t/p/w342",
                    "tr-TR"
            );
            TmdbMovieCatalogService movieCatalogService = new TmdbMovieCatalogService(properties);

            List<MovieSearchResponse> movies = movieCatalogService.search("yüzüklerin efendisi");

            assertEquals(1, movies.size());
            assertEquals(122L, movies.getFirst().tmdbId());
            assertEquals("Yüzüklerin Efendisi", movies.getFirst().title());
            assertEquals("2001", movies.getFirst().releaseYear());
            assertEquals("https://image.tmdb.org/t/p/w342/poster.jpg", movies.getFirst().posterUrl());
            assertEquals(8.4, movies.getFirst().voteAverage());
        } finally {
            fakeTmdbServer.stop(0);
        }
    }
}
