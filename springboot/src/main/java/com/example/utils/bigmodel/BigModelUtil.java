package com.example.utils.bigmodel;

import com.example.utils.Config;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.body.MultipartBody;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BigModelUtil {
    // 千问配置
    private static final String QWEN_API_KEY = Config.QWEN_API_KEY;
    private static final String QWEN_BASE_URL = Config.QWEN_BASE_URL;
    private static final String QWEN_MODEL = Config.QWEN_MODEL1;
    private static final String QWEN_DOC_MODEL = Config.QWEN_DOC_MODEL;

    // Kimi配置
    private static final String KIMI_API_KEY = Config.KIMI_API_KEY;
    private static final String KIMI_BASE_URL = Config.KIMI_BASE_URL;
    private static final String KIMI_MODEL = Config.KIMI_MODEL;
    private static final String KIMI_DOC_MODEL = Config.KIMI_DOC_MODEL;

    // DeepSeek配置
    private static final String DEEPSEEK_API_KEY = Config.DEEPSEEK_API_KEY;
    private static final String DEEPSEEK_BASE_URL = Config.DEEPSEEK_BASE_URL;
    private static final String DEEPSEEK_MODEL = Config.DEEPSEEK_MODEL;
    //星火配置
    private static final String SPARK_API_KEY = Config.SPARK_API_KEY;
    private static final String SPARK_BASE_URL = Config.SPARK_BASE_URL;
    private static final String SPARK_MODEL = Config.SPARK_MODEL;//

    private static final Gson gson = new Gson();

    public static String sparkTextGeneration(String content) throws Exception {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(createMessage("system", "You are a helpful assistant."));
        messages.add(createMessage("user", content));

        return sendRequest(SPARK_BASE_URL, SPARK_API_KEY, SPARK_MODEL, messages, null);
    }

    // 千问文字对话
    public static String qwenTextGeneration(String content) throws Exception {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(createMessage("system", "You are a helpful assistant."));
        messages.add(createMessage("user", content));

        return sendRequest(QWEN_BASE_URL, QWEN_API_KEY, QWEN_MODEL, messages, null);
    }

    // 千问文档理解（示例路径："C:\\Documents\\file.docx"）
    public static String qwenDocumentUnderstanding(String content, String filePath) throws Exception {
        return QwenDoc.kimidoc(filePath, content, QWEN_DOC_MODEL);
    }


    // Kimi文字对话
    public static String kimiTextGeneration(String content) throws Exception {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(createMessage("system", "You are a helpful assistant."));
        messages.add(createMessage("user", content));

        return sendRequest(KIMI_BASE_URL, KIMI_API_KEY, KIMI_MODEL, messages, 0.3f);
    }

    // Kimi文档理解（示例路径："C:\\Documents\\xlnet.pdf"）
    public static String kimiDocumentUnderstanding(String content, String filePath) throws Exception {
        File file = new File(filePath);
        String fileId = uploadFile(KIMI_BASE_URL, KIMI_API_KEY, file);
        String fileContent = getFileContent(KIMI_BASE_URL, KIMI_API_KEY, fileId);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(createMessage("system", fileContent));
        messages.add(createMessage("user", content));

        return sendRequest(KIMI_BASE_URL, KIMI_API_KEY, KIMI_DOC_MODEL, messages, 0.3f);
    }

    // DeepSeek文字对话
    public static String deepseekTextGeneration(String content) throws Exception {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(createMessage("system", "You are a helpful assistant."));
        messages.add(createMessage("user", content));
        return sendRequest(DEEPSEEK_BASE_URL, DEEPSEEK_API_KEY, DEEPSEEK_MODEL, messages, null);
    }

    private static Map<String, String> createMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private static String sendRequest(String baseUrl, String apiKey, String model,
                                      List<Map<String, String>> messages, Float temperature)
            throws Exception {
        JsonObject requestBody = new JsonObject();
        requestBody.add("model", gson.toJsonTree(model));
        requestBody.add("messages", gson.toJsonTree(messages));
        if (temperature != null) {
            requestBody.add("temperature", gson.toJsonTree(temperature));
        }

        HttpResponse<String> response = Unirest.post(baseUrl + "/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .body(requestBody.toString())
                .asString();

        return parseResponse(response.getBody());
    }


    private static String getFileContent(String baseUrl, String apiKey, String fileId) throws Exception {
        HttpResponse<String> response = Unirest.get(baseUrl + "/files/{fileId}/content")
                .routeParam("fileId", fileId)
                .header("Authorization", "Bearer " + apiKey)
                .asString();

        return response.getBody();
    }

    private static String uploadFile(String baseUrl, String apiKey, File file) throws Exception {
        MultipartBody request = Unirest.post(baseUrl + "/files")
                .header("Authorization", "Bearer " + apiKey)
                .field("file", file)
                .field("purpose", "file-extract");

        HttpResponse<String> response = request.asString();
        JsonObject jsonResponse = gson.fromJson(response.getBody(), JsonObject.class);
        return jsonResponse.get("id").getAsString();
    }


    private static String parseResponse(String jsonResponse) {
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
        return jsonObject.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .get("message").getAsJsonObject()
                .get("content").getAsString();
    }

    public static String getQwenApiKey() {
        return QWEN_API_KEY;
    }

    public static String getQwenBaseUrl() {
        return QWEN_BASE_URL;
    }

}