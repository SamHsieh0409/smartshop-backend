package com.smartshop.ai.impl;

import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartshop.ai.OllamaClient;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class OllamaClientImpl implements OllamaClient {

    private static final String OLLAMA_URL = "http://localhost:11434/api/chat";

    @Override
    public String ask(String systemPrompt, String userMessage) {

        OkHttpClient client = new OkHttpClient();

        JsonArray messages = new JsonArray();

        JsonObject sys = new JsonObject();
        sys.addProperty("role", "system");
        sys.addProperty("content", systemPrompt);

        JsonObject usr = new JsonObject();
        usr.addProperty("role", "user");
        usr.addProperty("content", userMessage);

        messages.add(sys);
        messages.add(usr);

        JsonObject req = new JsonObject();
        req.addProperty("model", "qwen2:4b");
        req.addProperty("stream", true);
        req.add("messages", messages);

        Request request = new Request.Builder()
                .url(OLLAMA_URL)
                .post(RequestBody.create(
                        req.toString(),
                        MediaType.parse("application/json")
                ))
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new RuntimeException("Ollama error: " + response.code());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));

            StringBuilder full = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {

                if (line.isBlank()) continue;

                JsonObject json = JsonParser.parseString(line).getAsJsonObject();

                if (json.has("message")) {
                    String chunk = json.get("message").getAsJsonObject().get("content").getAsString();

                    System.out.print(chunk);

                    full.append(chunk);
                }

                if (json.has("done") && json.get("done").getAsBoolean()) break;
            }

            return full.toString();

        } catch (Exception e) {
            throw new RuntimeException("Ollama Streaming Error: " + e.getMessage());
        }
    }

}
