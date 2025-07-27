package com.sw_pilot.mobility_server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw_pilot.mobility_server.domain.Data;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OpenAiService {
    private static final String API_KEY = ""; //여기에 key 넣기! 실제 키 없으면 null 또는 "" 됨
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String analyzeDataWithGpt(List<Data> dataList, Map<String, Integer> itemCounts) {
        //  API KEY 유무 체크
        if (API_KEY == null || API_KEY.isBlank()) {
            return "AI 분석 데이터를 받아오지 못했습니다 (API Key 미설정)";
        }

        try {
            StringBuilder promptBuilder = new StringBuilder();
            promptBuilder.append("다음은 서버에 수집된 센서 데이터 상황입니다. 이를 한글로 간결하게 요약 분석해줘.\n");

            for (Data data : dataList) {
                promptBuilder.append(
                        String.format("%s %s %s %d회\n",
                                data.getTime(),
                                data.getAreaName(),
                                data.getItemName(),
                                data.getValue())
                );
            }

            promptBuilder.append("각 항목별 총 발생 횟수는 다음과 같습니다.\n");
            for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
                promptBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("회\n");
            }
            promptBuilder.append("이 데이터의 특이사항, 패턴, 주의할 점을 요약해줘.");

            String requestBody = mapper.writeValueAsString(Map.of(
                    "model", "gpt-4o",
                    "messages", List.of(
                            Map.of(
                                    "role", "system",
                                    "content", "당신은 데이터 분석 전문가입니다."
                            ),
                            Map.of(
                                    "role", "user",
                                    "content", promptBuilder.toString()
                            )
                    )
            ));

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                System.out.println("Status Code: " + response.code());
                System.out.println("Response Body: " + responseBody);

                if (!response.isSuccessful()) {
                    return "AI 분석 데이터를 받아오지 못했습니다 (" + response.code() + ")";
                }
                JsonNode jsonNode = mapper.readTree(responseBody);
                return jsonNode
                        .path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "AI 분석 데이터를 받아오지 못했습니다 (" + e.getClass().getSimpleName() + ")";
        }
    }
}