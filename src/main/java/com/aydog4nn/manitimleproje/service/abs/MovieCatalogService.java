package com.aydog4nn.manitimleproje.service.abs;

import com.aydog4nn.manitimleproje.dto.movie.MovieSearchResponse;
import java.util.List;

public interface MovieCatalogService {
    List<MovieSearchResponse> search(String query);
}
