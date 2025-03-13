package com.example.utils.bigmodel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class QwenDoc {
    private static final String API_KEY = BigModelUtil.getQwenApiKey();
    private static final String BASE_URL = BigModelUtil.getQwenBaseUrl();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(180, TimeUnit.SECONDS)  // Set connection timeout
            .readTimeout(180, TimeUnit.SECONDS)     // Set read timeout
            .writeTimeout(180, TimeUnit.SECONDS)    // Set write timeout
            .build();
    private static final Gson gson = new Gson();

    public static String kimidoc(String filepath, String content, String model) throws IOException {
        String fileId = uploadFile(filepath);
        return getChatCompletion(fileId, content, model);
    }

    private static String uploadFile(String fileurl) throws IOException {
        // 1. 构建文件上传请求
        File file = new File(fileurl);
        RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));

        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("purpose", "file-extract")
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/files")
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        // 2. 执行请求并解析响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("文件上传失败: " + response);

            JsonObject jsonResponse = JsonParser.parseString(response.body().string()).getAsJsonObject();
            return jsonResponse.get("id").getAsString();
        }
    }

    private static String getChatCompletion(String fileId, String content, String model) throws IOException {
        // 1. 构建请求体
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "fileid://" + fileId);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", content);

        JsonArray messages = new JsonArray();
        messages.add(systemMessage);
        messages.add(userMessage);

        JsonObject parameters = new JsonObject();
        parameters.addProperty("result_format", "message");

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.add("messages", messages);
        requestBody.add("parameters", parameters);

        // 2. 构建HTTP请求
        Request request = new Request.Builder()
                .url(BASE_URL + "/chat/completions")
                .header("Authorization", "Bearer " + API_KEY)
                .post(RequestBody.create(gson.toJson(requestBody), JSON))
                .build();

        // 3. 执行请求并输出结果
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("请求失败: " + response);

            if (response.body() != null) {
                String jsonResponse = response.body().string();
                return parseResponse(jsonResponse); // 关键修改：调用解析方法
            } else {
                return "返回了空字符串";
            }
        }
    }

    private static String parseResponse(String jsonResponse) {
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
        return jsonObject.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .get("message").getAsJsonObject()
                .get("content").getAsString();
    }
}
