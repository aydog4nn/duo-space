package com.aydog4nn.manitimleproje.config;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationFilterTest {

    // Yalnızca testte kullanılan anahtar; uygulamanın ortam ayarını okumuyoruz.
    private final String testSecret = Base64.getEncoder().encodeToString(new byte[64]);
    private final JwtService jwtService = new JwtService(testSecret, Duration.ofMinutes(15));
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenHeaderIsMissing() throws Exception {
        AtomicBoolean continued = new AtomicBoolean();

        filter.doFilter(new MockHttpServletRequest(), response, (request, result) -> {
            continued.set(true);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        });

        assertTrue(continued.get());
    }

    @Test
    void shouldAuthenticateAValidToken() throws Exception {
        UUID userId = UUID.randomUUID();
        AtomicBoolean continued = new AtomicBoolean();

        filter.doFilter(bearer(jwtService.generateAccessToken(userId, "test-user")), response,
                (request, result) -> {
                    continued.set(true);
                    assertEquals(userId.toString(),
                            SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                });

        assertTrue(continued.get());
        assertEquals(200, response.getStatus());
    }

    @Test
    void shouldRejectAMalformedToken() throws Exception {
        assertRejected("invalid-token");
    }

    @Test
    void shouldRejectAnExpiredToken() throws Exception {
        JwtService expiredJwtService = new JwtService(testSecret, Duration.ofMinutes(-1));
        assertRejected(expiredJwtService.generateAccessToken(UUID.randomUUID(), "test-user"));
    }

    @Test
    void shouldNotConvertDownstreamArgumentErrorsIntoTokenErrors() throws Exception {
        IllegalArgumentException failure = new IllegalArgumentException("İşlem girdisi hatalı");

        assertDownstreamFailureIsPreserved(failure);
    }

    @Test
    void shouldNotConvertDownstreamJwtErrorsIntoAccessTokenErrors() throws Exception {
        JwtException failure = new JwtException("Sonraki bileşende oluşan hata");

        assertDownstreamFailureIsPreserved(failure);
    }

    private void assertDownstreamFailureIsPreserved(RuntimeException failure) throws Exception {
        String token = jwtService.generateAccessToken(UUID.randomUUID(), "test-user");

        RuntimeException thrown = assertThrows(failure.getClass(), () ->
                filter.doFilter(bearer(token), response, (request, result) -> {
                    throw failure;
                }));

        assertSame(failure, thrown);
        assertEquals(200, response.getStatus());
        assertEquals("", response.getContentAsString());
    }

    private void assertRejected(String token) throws Exception {
        filter.doFilter(bearer(token), response, (request, result) ->
                fail("Geçersiz token ile işlem devam etmemeli"));

        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("{\"message\":\"Geçersiz veya süresi dolmuş erişim tokenı.\"}",
                response.getContentAsString());
    }

    private MockHttpServletRequest bearer(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return request;
    }
}
