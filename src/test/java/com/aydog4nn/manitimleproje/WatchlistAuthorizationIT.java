package com.aydog4nn.manitimleproje;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.jpa.show-sql=false", "server.address=127.0.0.1",
                "logging.level.org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration=ERROR"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WatchlistAuthorizationIT {
    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final Map<String, String> NEW_ITEM = Map.of("title", "İlk film");
    private static final Map<String, String> UPDATE = Map.of("title", "Yeni film", "status", "WATCHING");
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    @Value("${local.server.port}") private int serverPort;
    private String ownerToken;
    private String memberToken;
    private String outsiderToken;
    private String roomId;
    private String otherRoomId;
    private String itemId;

    @DynamicPropertySource
    static void testDatabase(DynamicPropertyRegistry registry) {
        String port = System.getenv("DUO_TEST_DB_PORT");
        if (port == null || !port.matches("[0-9]{1,5}") || Integer.parseInt(port) < 1
                || Integer.parseInt(port) > 65535) {
            throw new IllegalStateException("DUO_TEST_DB_PORT ayrı test PostgreSQL portuna ayarlanmalı.");
        }
        // Uygulamanın DB_URL veya gerçek JWT anahtarına geri dönüş yok.
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://127.0.0.1:" + port + "/duospace_auth_test");
        registry.add("spring.datasource.username", () -> "duo_test");
        registry.add("spring.datasource.password", () -> "local-test-only");
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        String secret = Base64.getEncoder().encodeToString(key);
        registry.add("app.jwt.secret", () -> secret);
    }

    @BeforeAll
    void createAccountsAndRooms() throws Exception {
        ownerToken = registerAndLogin();
        memberToken = registerAndLogin();
        outsiderToken = registerAndLogin();
        JsonNode room = call("POST", "/rooms", ownerToken, Map.of("name", "Test odası"), 201);
        roomId = room.get("id").asText();
        call("POST", "/rooms/join", memberToken, Map.of("inviteCode", room.get("inviteCode").asText()), 200);
        otherRoomId = call("POST", "/rooms", ownerToken, Map.of("name", "İkinci oda"), 201).get("id").asText();
        call("POST", "/rooms", outsiderToken, Map.of("name", "Başka kullanıcının odası"), 201);
    }

    @BeforeEach
    void createItem() throws Exception {
        itemId = call("POST", listPath(roomId), ownerToken, NEW_ITEM, 201).get("id").asText();
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST", "PUT", "DELETE"})
    void anonymousRequestsAreRejected(String method) throws Exception {
        call(method, pathFor(method, roomId), null, bodyFor(method), 401);
        assertOriginalItemExists();
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST", "PUT", "DELETE"})
    void anotherRoomsUserCannotAccessTheList(String method) throws Exception {
        call(method, pathFor(method, roomId), outsiderToken, bodyFor(method), 403);
        assertOriginalItemExists();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PUT", "DELETE"})
    void ownerCannotUseTheWrongRoomPath(String method) throws Exception {
        call(method, pathFor(method, otherRoomId), ownerToken, bodyFor(method), 404);
        assertOriginalItemExists();
    }

    @Test
    void invitedMemberCanUpdateAndDeleteTheSharedItem() throws Exception {
        call("PUT", pathFor("PUT", roomId), memberToken, UPDATE, 200);
        JsonNode saved = findItem(ownerToken);
        assertEquals("Yeni film", saved.get("title").asText());
        assertEquals("WATCHING", saved.get("status").asText());
        call("DELETE", pathFor("DELETE", roomId), memberToken, null, 204);
        assertEquals(null, findItem(ownerToken));
    }

    @Test
    void invalidTokenIsRejected() throws Exception {
        call("GET", listPath(roomId), "invalid-token", null, 401);
        assertOriginalItemExists();
    }

    private String registerAndLogin() throws Exception {
        String name = "test-" + UUID.randomUUID();
        String email = name + "@example.test";
        String password = UUID.randomUUID().toString();
        call("POST", "/auth/register", null, Map.of("username", name, "email", email, "password", password), 201);
        return call("POST", "/auth/login", null, Map.of("email", email, "password", password), 200)
                .get("accessToken").asText();
    }

    private void assertOriginalItemExists() throws Exception {
        assertEquals("İlk film", findItem(ownerToken).get("title").asText());
    }

    private JsonNode findItem(String token) throws Exception {
        for (JsonNode item : call("GET", listPath(roomId), token, null, 200)) {
            if (itemId.equals(item.get("id").asText())) return item;
        }
        return null;
    }

    private String listPath(String room) { return "/rooms/" + room + "/watchlist"; }

    private String pathFor(String method, String room) {
        return listPath(room) + (method.equals("PUT") || method.equals("DELETE") ? "/" + itemId : "");
    }

    private Object bodyFor(String method) {
        return switch (method) { case "POST" -> NEW_ITEM; case "PUT" -> UPDATE; default -> null; };
    }

    private JsonNode call(String method, String path, String token, Object body, int expected) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + serverPort + "/api/v1" + path))
                .timeout(Duration.ofSeconds(10)).header("Content-Type", "application/json");
        if (token != null) request.header("Authorization", "Bearer " + token);
        request.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)));
        HttpResponse<String> response = client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        // Başarısız test çıktısına token veya kayıt/giriş gövdesi yazma.
        assertEquals(expected, response.statusCode(), method + " " + path);
        return response.body().isBlank() ? null : JSON.readTree(response.body());
    }
}
