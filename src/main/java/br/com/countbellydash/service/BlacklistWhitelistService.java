package br.com.countbellydash.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public abstract class BlacklistWhitelistService {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_URL = dotenv.get("API_URL");
    private static final String BOT_USERNAME = dotenv.get("BOT_USERNAME");
    private static final String BOT_PASSWORD = dotenv.get("BOT_PASSWORD");

    // CORREÇÃO 1: Forçar versão HTTP_1_1 para evitar erros de handshake com servidores Python/Node simples
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static String getAuthorizationHeader() {
        if (BOT_USERNAME == null || BOT_USERNAME.isEmpty() ||
                BOT_PASSWORD == null || BOT_PASSWORD.isEmpty()) {
            System.err.println("BOT_USERNAME ou BOT_PASSWORD não configurados no ambiente");
            return null;
        }
        String credentials = BOT_USERNAME + ":" + BOT_PASSWORD;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private static String getApiUrl() {
        if (API_URL == null || API_URL.isEmpty()) {
            System.err.println("API_URL não configurada no ambiente");
            return null;
        }
        // Remove barra final se existir para evitar duplicidade (ex: //api/v1)
        return API_URL.endsWith("/") ? API_URL.substring(0, API_URL.length() - 1) : API_URL;
    }

    // --- Whitelist Operations ---

    public static List<String> getWhitelist() {
        // (Mantive igual, apenas verifique se precisa do header Accept aqui também)
        String apiUrl = getApiUrl();
        String authHeader = getAuthorizationHeader();
        if (apiUrl == null || authHeader == null) return Collections.emptyList();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/api/v1/admin/whitelist"))
                    .header("Authorization", authHeader)
                    .header("Accept", "application/json") // Boa prática incluir
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<List<String>>() {});
            } else {
                System.err.println("Erro GET Whitelist: " + response.statusCode() + " - " + response.body());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static String postWhitelist(List<String> userIds) {
        String apiUrl = getApiUrl();
        String authHeader = getAuthorizationHeader();
        if (apiUrl == null || authHeader == null) return "CONFIG_ERROR";

        try {
            String requestBody = objectMapper.writeValueAsString(new UserIdsWrapper(userIds));
            System.out.println("POST Request Body: " + requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/api/v1/admin/whitelist"))
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json") // CORREÇÃO 2: Adicionado header Accept igual ao CURL
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("POST Response: " + response.statusCode() + " - " + response.body());

            if (response.statusCode() == 200) {
                return "SUCCESS: Whitelist updated.";
            } else {
                return "API_ERROR: " + response.statusCode() + " - " + response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "API_CONNECTION_ERROR: " + e.getMessage();
        }
    }

    public static String deleteWhitelist(List<String> userIds) {
        String apiUrl = getApiUrl();
        String authHeader = getAuthorizationHeader();
        if (apiUrl == null || authHeader == null) return "CONFIG_ERROR";

        try {
            String requestBody = objectMapper.writeValueAsString(new UserIdsWrapper(userIds));
            System.out.println("DELETE Request Body: " + requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/api/v1/admin/whitelist"))
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json") // CORREÇÃO 2
                    .method("DELETE", HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("DELETE Response: " + response.statusCode() + " - " + response.body());

            if (response.statusCode() == 200) {
                return "SUCCESS: Whitelist updated (removed).";
            } else {
                return "API_ERROR: " + response.statusCode() + " - " + response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "API_CONNECTION_ERROR: " + e.getMessage();
        }
    }
    // --- Blacklist Operations ---

    public static List<String> getBlacklist() {
        String apiUrl = getApiUrl();
        String authHeader = getAuthorizationHeader();
        if (apiUrl == null || authHeader == null) return Collections.emptyList();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/api/v1/admin/blacklist"))
                    .header("Authorization", authHeader)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), new TypeReference<List<String>>() {});
            } else {
                System.err.println("Erro GET Blacklist: " + response.statusCode());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static String postBlacklist(List<String> userIds) {
        String apiUrl = getApiUrl();
        String authHeader = getAuthorizationHeader();
        if (apiUrl == null || authHeader == null) return "CONFIG_ERROR";

        try {
            // Garante serialização {"user_ids": ["123"]}
            String requestBody = objectMapper.writeValueAsString(new UserIdsWrapper(userIds));
            System.out.println("POST Blacklist Req: " + requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/api/v1/admin/blacklist"))
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json") // Header crucial
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("POST Blacklist Res: " + response.statusCode() + " - " + response.body());

            if (response.statusCode() == 200) {
                return "SUCCESS: Blacklist updated.";
            } else {
                return "API_ERROR: " + response.statusCode() + " - " + response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "API_CONNECTION_ERROR: " + e.getMessage();
        }
    }

    public static String deleteBlacklist(List<String> userIds) {
        String apiUrl = getApiUrl();
        String authHeader = getAuthorizationHeader();
        if (apiUrl == null || authHeader == null) return "CONFIG_ERROR";

        try {
            String requestBody = objectMapper.writeValueAsString(new UserIdsWrapper(userIds));
            System.out.println("DELETE Blacklist Req: " + requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/api/v1/admin/blacklist"))
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json") // Header crucial
                    .method("DELETE", HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("DELETE Blacklist Res: " + response.statusCode() + " - " + response.body());

            if (response.statusCode() == 200) {
                return "SUCCESS: Blacklist items removed.";
            } else {
                return "API_ERROR: " + response.statusCode() + " - " + response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "API_CONNECTION_ERROR: " + e.getMessage();
        }
    }

    // --- Mode Operations ---

    public static String getMode() {
        String apiUrl = getApiUrl();
        String authHeader = getAuthorizationHeader();
        if (apiUrl == null || authHeader == null) return "CONFIG_ERROR";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/api/v1/admin/mode"))
                    .header("Authorization", authHeader)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ModeWrapper wrapper = objectMapper.readValue(response.body(), ModeWrapper.class);
                return wrapper.mode;
            } else {
                return "API_ERROR: " + response.statusCode() + " - " + response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "API_CONNECTION_ERROR: " + e.getMessage();
        }
    }

    public static String postMode(String mode) {
        String apiUrl = getApiUrl();
        String authHeader = getAuthorizationHeader();
        if (apiUrl == null || authHeader == null) return "CONFIG_ERROR";

        try {
            // Garante serialização {"mode": "whitelist"}
            String requestBody = objectMapper.writeValueAsString(new ModeWrapper(mode));
            System.out.println("POST Mode Req: " + requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "/api/v1/admin/mode"))
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("POST Mode Res: " + response.statusCode() + " - " + response.body());

            if (response.statusCode() == 200) {
                return "SUCCESS: Mode updated to " + mode + ".";
            } else {
                return "API_ERROR: " + response.statusCode() + " - " + response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "API_CONNECTION_ERROR: " + e.getMessage();
        }
    }
    private static class UserIdsWrapper {

        @JsonProperty("user_ids")
        private List<String> userIds;

        // Construtor vazio necessário para o Jackson (caso precise deserializar algum dia)
        public UserIdsWrapper() {}

        public UserIdsWrapper(List<String> userIds) {
            this.userIds = userIds;
        }

        public List<String> getUserIds() {
            return userIds;
        }

        public void setUserIds(List<String> userIds) {
            this.userIds = userIds;
        }
    }

    private static class ModeWrapper {
        @JsonProperty("mode")
        public String mode;

        public ModeWrapper() {} // Construtor padrão

        public ModeWrapper(String mode) {
            this.mode = mode;
        }
    }
}