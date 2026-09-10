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

public class SupabaseClient {

    public static final String SUPABASE_URL = "https://leneusmnmwxmuyjxfqjx.supabase.co";
    public static final String SUPABASE_ANON_KEY = "sb_publishable_FBGOzmhk8Lk2xLbPQCXEkA_g0vFAs6j";

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static String currentAccessToken = "";
    private static String currentUserId = "";

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
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

    // --- Authentication ---

    public static void signUp(final String name, final String email, final String password, final Callback<JSONObject> callback) {
        executor.execute(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("email", email.trim().toLowerCase());
                body.put("password", password);

                JSONObject meta = new JSONObject();
                meta.put("name", name.trim());
                body.put("data", meta);

                String response = sendHttpRequest("POST", SUPABASE_URL + "/auth/v1/signup", body.toString(), null);
                JSONObject json = new JSONObject(response);
                callback.onSuccess(json);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void signIn(final String email, final String password, final Callback<JSONObject> callback) {
        executor.execute(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("email", email.trim().toLowerCase());
                body.put("password", password);

                String response = sendHttpRequest("POST", SUPABASE_URL + "/auth/v1/token?grant_type=password", body.toString(), null);
                JSONObject json = new JSONObject(response);

                if (json.has("access_token")) {
                    String token = json.getString("access_token");
                    String userId = "";
                    if (json.has("user") && json.getJSONObject("user").has("id")) {
                        userId = json.getJSONObject("user").getString("id");
                    }
                    setSession(token, userId);
                }

                callback.onSuccess(json);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // --- Task CRUD Operations ---

    public static void fetchTasks(final Callback<JSONArray> callback) {
        executor.execute(() -> {
            try {
                String endpoint = SUPABASE_URL + "/rest/v1/tasks?select=*&order=is_completed.asc,created_at.desc";
                String response = sendHttpRequest("GET", endpoint, null, currentAccessToken);
                JSONArray array = new JSONArray(response);
                callback.onSuccess(array);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void insertTask(final String title, final String notes, final Callback<JSONObject> callback) {
        executor.execute(() -> {
            try {
                String endpoint = SUPABASE_URL + "/rest/v1/tasks";
                JSONObject body = new JSONObject();
                body.put("title", title.trim());
                body.put("notes", notes != null ? notes.trim() : "");
                body.put("is_completed", false);
                if (!currentUserId.isEmpty()) {
                    body.put("user_id", currentUserId);
                }

                String response = sendHttpRequest("POST", endpoint, body.toString(), currentAccessToken);
                JSONArray array = new JSONArray(response);
                JSONObject created = array.length() > 0 ? array.getJSONObject(0) : new JSONObject();
                callback.onSuccess(created);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void updateTaskStatus(final long taskId, final boolean isCompleted, final Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                String endpoint = SUPABASE_URL + "/rest/v1/tasks?id=eq." + taskId;
                JSONObject body = new JSONObject();
                body.put("is_completed", isCompleted);

                sendHttpRequest("PATCH", endpoint, body.toString(), currentAccessToken);
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void deleteTask(final long taskId, final Callback<Boolean> callback) {
        executor.execute(() -> {
            try {
                String endpoint = SUPABASE_URL + "/rest/v1/tasks?id=eq." + taskId;
                sendHttpRequest("DELETE", endpoint, null, currentAccessToken);
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // --- Pure Java HTTP Helper ---

    private static String sendHttpRequest(String method, String urlString, String jsonBody, String bearerToken) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);

        conn.setRequestProperty("apikey", SUPABASE_ANON_KEY);
        String authHeader = (bearerToken != null && !bearerToken.isEmpty()) ? "Bearer " + bearerToken : "Bearer " + SUPABASE_ANON_KEY;
        conn.setRequestProperty("Authorization", authHeader);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Prefer", "return=representation");

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
    }
}
