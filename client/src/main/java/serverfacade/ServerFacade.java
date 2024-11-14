package serverfacade;

import com.google.gson.Gson;
import request.*;
import result.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public void clear() {
        String path = "/db";
        try {
            makeRequest("DELETE", path, null, null, null);
        }
        catch(Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public LoginResult register(RegisterRequest registerRequest) throws ServiceException {
        String path = "/user";
        return makeRequest("POST", path, registerRequest, null, LoginResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws ServiceException {
        String path = "/session";
        return makeRequest("POST", path, loginRequest, null, LoginResult.class);
    }

    public void logout(String authToken) throws ServiceException {
        String path = "/session";
        makeRequest("DELETE", path, null, authToken, null);
    }

    public ListGamesResult listGames(String authToken) throws ServiceException {
        String path = "/game";
        return makeRequest("GET", path, null, authToken, ListGamesResult.class);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest createGameRequest) throws ServiceException {
        String path = "/game";
        return makeRequest("POST", path, createGameRequest, authToken, CreateGameResult.class);
    }

    public void joinGame(String authToken, JoinGameRequest joinGameRequest) throws ServiceException {
        String path = "/game";
        makeRequest("PUT", path, joinGameRequest, authToken, null);
    }

    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass) throws ServiceException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeHeader(authToken, http);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex.getMessage(), 500);
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static void writeHeader(String authToken, HttpURLConnection http) throws IOException {
        if(authToken != null) {
            http.addRequestProperty("authorization", authToken);
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ServiceException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ServiceException("failure: " + status, status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
