package com.oibsip.todo.supabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SupabaseClient {

    public static final String SUPABASE_URL = "https://leneusmnmwxmuyjxfqjx.supabase.co";
    public static final String SUPABASE_ANON_KEY = "sb_publishable_FBGOzmhk8Lk2xLbPQCXEkA_g0vFAs6j";

    private static final int TIMEOUT_MILLIS = 8000;
    private static final String HEADER_API_KEY = "apikey";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_PREFER = "Prefer";
    private static final String MEDIA_TYPE_JSON = "application/json";
    private static final String PREFER_RETURN_REPRESENTATION = "return=representation";

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static volatile String currentAccessToken = "";
    private static volatile String currentUserId = "";

    private SupabaseClient() {}

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    @FunctionalInterface
    private interface AsyncOperation<T> {
        T execute() throws Exception;
    }

    private static <T> void executeAsync(final Callback<T> callback, final AsyncOperation<T> operation) {
        executor.execute(() -> {
            try {
                T result = operation.execute();
                if (callback != null) {
                    callback.onSuccess(result);
                }
            } catch (Exception e) {
                if (callback != null) {
                    String message = e.getMessage();
                    callback.onError(message != null && !message.trim().isEmpty() ? message : e.getClass().getSimpleName());
                }
            }
        });
    }

    public static void setSession(String accessToken, String userId) {
        currentAccessToken = accessToken != null ? accessToken : "";
        currentUserId = userId != null ? userId : "";
    }

    public static String getAccessToken() {
        return currentAccessToken;
    }

    public static String getUserId() {
        return currentUserId;
    }

    public static boolean hasActiveSession() {
        return !currentAccessToken.isEmpty() && !currentUserId.isEmpty();
    }

    public static void clearSession() {
        currentAccessToken = "";
        currentUserId = "";
    }

    public static void signUp(final String name, final String email, final String password, final Callback<JSONObject> callback) {
        executeAsync(callback, () -> {
            String safeEmail = email != null ? email.trim().toLowerCase() : "";
            String safeName = name != null ? name.trim() : "";
            if (safeEmail.isEmpty() || password == null || password.isEmpty()) {
                throw new IllegalArgumentException("Email and password cannot be empty");
            }

            JSONObject body = new JSONObject();
            body.put("email", safeEmail);
            body.put("password", password);

            JSONObject meta = new JSONObject();
            meta.put("name", safeName);
            body.put("data", meta);

            String endpoint = SUPABASE_URL + "/auth/v1/signup";
            String response = sendHttpRequest("POST", endpoint, body.toString(), null);
            return new JSONObject(response);
        });
    }

    public static void signIn(final String email, final String password, final Callback<JSONObject> callback) {
        executeAsync(callback, () -> {
            String safeEmail = email != null ? email.trim().toLowerCase() : "";
            if (safeEmail.isEmpty() || password == null || password.isEmpty()) {
                throw new IllegalArgumentException("Email and password cannot be empty");
            }

            JSONObject body = new JSONObject();
            body.put("email", safeEmail);
            body.put("password", password);

            String endpoint = SUPABASE_URL + "/auth/v1/token?grant_type=password";
            String response = sendHttpRequest("POST", endpoint, body.toString(), null);
            JSONObject json = new JSONObject(response);

            if (json.has("access_token")) {
                String token = json.getString("access_token");
                String userId = "";
                if (json.has("user")) {
                    JSONObject userObj = json.optJSONObject("user");
                    if (userObj != null) {
                        userId = userObj.optString("id", "");
                    }
                }
                setSession(token, userId);
            }

            return json;
        });
    }

    public static void fetchTasks(final Callback<JSONArray> callback) {
        executeAsync(callback, () -> {
            String endpoint = SUPABASE_URL + "/rest/v1/tasks?select=*&order=is_completed.asc,created_at.desc";
            String response = sendHttpRequest("GET", endpoint, null, currentAccessToken);
            return new JSONArray(response);
        });
    }

    public static void insertTask(final String title, final String notes, final Callback<JSONObject> callback) {
        executeAsync(callback, () -> {
            String safeTitle = title != null ? title.trim() : "";
            if (safeTitle.isEmpty()) {
                throw new IllegalArgumentException("Task title cannot be empty");
            }
            String safeNotes = notes != null ? notes.trim() : "";

            JSONObject body = new JSONObject();
            body.put("title", safeTitle);
            body.put("notes", safeNotes);
            body.put("is_completed", false);
            if (!currentUserId.isEmpty()) {
                body.put("user_id", currentUserId);
            }

            String endpoint = SUPABASE_URL + "/rest/v1/tasks";
            String response = sendHttpRequest("POST", endpoint, body.toString(), currentAccessToken);
            JSONArray array = new JSONArray(response);
            return array.length() > 0 ? array.getJSONObject(0) : new JSONObject();
        });
    }

    public static void updateTaskStatus(final long taskId, final boolean isCompleted, final Callback<Boolean> callback) {
        executeAsync(callback, () -> {
            String endpoint = SUPABASE_URL + "/rest/v1/tasks?id=eq." + taskId;
            JSONObject body = new JSONObject();
            body.put("is_completed", isCompleted);

            sendHttpRequest("PATCH", endpoint, body.toString(), currentAccessToken);
            return true;
        });
    }

    public static void deleteTask(final long taskId, final Callback<Boolean> callback) {
        executeAsync(callback, () -> {
            String endpoint = SUPABASE_URL + "/rest/v1/tasks?id=eq." + taskId;
            sendHttpRequest("DELETE", endpoint, null, currentAccessToken);
            return true;
        });
    }

    private static String sendHttpRequest(String method, String urlString, String jsonBody, String bearerToken) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(TIMEOUT_MILLIS);
            conn.setReadTimeout(TIMEOUT_MILLIS);

            conn.setRequestProperty(HEADER_API_KEY, SUPABASE_ANON_KEY);
            String authHeader = (bearerToken != null && !bearerToken.isEmpty()) ? "Bearer " + bearerToken : "Bearer " + SUPABASE_ANON_KEY;
            conn.setRequestProperty(HEADER_AUTHORIZATION, authHeader);
            conn.setRequestProperty(HEADER_CONTENT_TYPE, MEDIA_TYPE_JSON);
            conn.setRequestProperty(HEADER_ACCEPT, MEDIA_TYPE_JSON);
            conn.setRequestProperty(HEADER_PREFER, PREFER_RETURN_REPRESENTATION);

            if (jsonBody != null && (method.equals("POST") || method.equals("PATCH") || method.equals("PUT"))) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }
            }

            int code = conn.getResponseCode();
            InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();

            if (is == null) {
                if (code >= 200 && code < 300) return "[]";
                throw new RuntimeException("HTTP error code: " + code);
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            if (code >= 400) {
                throw new RuntimeException("Supabase HTTP " + code + ": " + response.toString());
            }

            return response.toString();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
