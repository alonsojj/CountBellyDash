package br.com.countbellydash.service;

import br.com.countbellydash.model.DashboardData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import io.github.cdimascio.dotenv.Dotenv;

abstract public class SessionService {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_URL = dotenv.get("API_URL");
    private static final String BOT_USERNAME = dotenv.get("BOT_USERNAME");
    private static final String BOT_PASSWORD = dotenv.get("BOT_PASSWORD");

    private static final HttpClient httpClient = HttpClient.newHttpClient();;




    static public DashboardData buildDashboardData(){
        String json = fetchSessionsJson();
        return new DashboardData(json);
    }
    static public String fetchSessionsJson() {
        if (API_URL == null || API_URL.isEmpty()) {
            System.err.println("API_URL não configurada no ambiente");
            return "CONFIG_ERROR: API_URL";
        }
        if (BOT_USERNAME == null || BOT_USERNAME.isEmpty() ||
                BOT_PASSWORD == null || BOT_PASSWORD.isEmpty()) {
            System.err.println("BOT_USERNAME ou BOT_PASSWORD não configurados no ambiente");
            return "CONFIG_ERROR: BOT_CREDENTIALS";
        }

        try {
            String credentials = BOT_USERNAME + ":" + BOT_PASSWORD;
            String encodedAuth = Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/api/v1/admin/sessions"))
                    .header("Authorization", "Basic " + encodedAuth)
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("DEBUG: Raw JSON response from /api/v1/admin/sessions: " + response.body());

            if (response.statusCode() == 200) {
                return response.body(); // JSON bruto
            } else {
                System.err.println("Erro ao buscar sessões: Status " + response.statusCode());
                System.err.println("Corpo da resposta: " + response.body());
                return "API_STATUS_ERROR: " + response.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Exceção ao conectar ou processar a API de sessões: " + e.getMessage());
            e.printStackTrace();
            return "API_CONNECTION_ERROR: " + e.getMessage();
        }
    }

    static public String deleteSession(String userId) {
        if (API_URL == null || API_URL.isEmpty()) {
            System.err.println("API_URL não configurada no ambiente");
            return "CONFIG_ERROR: API_URL";
        }
        if (BOT_USERNAME == null || BOT_USERNAME.isEmpty() ||
                BOT_PASSWORD == null || BOT_PASSWORD.isEmpty()) {
            System.err.println("BOT_USERNAME ou BOT_PASSWORD não configurados no ambiente");
            return "CONFIG_ERROR: BOT_CREDENTIALS";
        }
        if (userId == null || userId.isEmpty()) {
            System.err.println("userId não pode ser nulo ou vazio para deletar a sessão");
            return "CLIENT_ERROR: userId is null or empty";
        }

        try {
            String credentials = BOT_USERNAME + ":" + BOT_PASSWORD;
            String encodedAuth = Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            String requestUrl = API_URL + "/api/v1/admin/session/" + userId;
            System.out.println("DEBUG: DELETE request to: " + requestUrl);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("Authorization", "Basic " + encodedAuth)
                    .DELETE()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("DEBUG: DELETE response status for " + userId + ": " + response.statusCode());
            System.out.println("DEBUG: DELETE response body for " + userId + ": " + response.body());

            if (response.statusCode() == 200 || response.statusCode() == 204) {
                return "SUCCESS: Session for user " + userId + " deleted successfully.";
            } else {
                System.err.println("Erro ao deletar sessão para o usuário " + userId + ": Status " + response.statusCode());
                System.err.println("Corpo da resposta: " + response.body());
                return "API_STATUS_ERROR: " + response.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Exceção ao conectar ou processar a API de deleção de sessão: " + e.getMessage());
            e.printStackTrace();
            return "API_CONNECTION_ERROR: " + e.getMessage();
        }
    }
}