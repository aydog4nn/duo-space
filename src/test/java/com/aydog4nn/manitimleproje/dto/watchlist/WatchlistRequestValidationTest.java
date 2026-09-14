package com.aydog4nn.manitimleproje.dto.watchlist;

import com.aydog4nn.manitimleproje.entity.enums.WatchlistStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WatchlistRequestValidationTest {
    private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = FACTORY.getValidator();

    @AfterAll
    static void closeFactory() {
        FACTORY.close();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"https://example.com/movie/1", "http://example.com", "HTTPS://example.com/a?q=film#detail"})
    void shouldAcceptOptionalOrValidWebLinks(String url) {
        assertTrue(VALIDATOR.validate(new CreateWatchlistItemRequest("Film", url)).isEmpty());
        assertTrue(VALIDATOR.validate(new UpdateWatchlistItemRequest("Film", url, WatchlistStatus.PLANNED)).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"javascript:alert(1)", "data:text/html,test", "file:///etc/passwd", "ftp://example.com",
            "//example.com", "/movie/1", "https:///movie", "https://", "https://user:pass@example.com",
            "https://example.com:99999", "https://example.com:abc", "https://example.com/a b", " ",
            "https://example.com/\nmovie"})
    void shouldRejectInvalidOrNonWebLinks(String url) {
        assertFalse(VALIDATOR.validate(new CreateWatchlistItemRequest("Film", url)).isEmpty());
        assertFalse(VALIDATOR.validate(new UpdateWatchlistItemRequest("Film", url, WatchlistStatus.PLANNED)).isEmpty());
    }
}
