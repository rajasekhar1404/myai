package com.myai.connector;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Map;

@Component
public class AtlassianConnector {

    @Value("${atlassian.email}")
    private String email;

    @Value("${atlassian.password}")
    private String password;

    public JsonNode getAtlassianResponse(String url, @Nullable Map<String, Object> queryParams) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(url)
                    .basicAuth(email, password)
                    .header("Accept", "application/json")
                    .queryString(queryParams)
                    .asJson();
            return response.getBody();
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return new JsonNode("");
        }
    }

    public InputStream getAtlassianBinaryResponse(String url, @Nullable Map<String, Object> queryParams) {
        try {
            HttpResponse<InputStream> response = Unirest.get(url)
                    .basicAuth(email, password)
                    .header("Accept", "application/json")
                    .queryString(queryParams)
                    .asBinary();
            return response.getBody();
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return null;
        }
    }

}
