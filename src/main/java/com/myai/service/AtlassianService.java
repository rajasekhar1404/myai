package com.myai.service;

import com.mashape.unirest.http.JsonNode;
import com.myai.connector.AtlassianConnector;
import com.myai.constants.MyAiConstants;
import dev.langchain4j.data.document.Document;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.myai.constants.MyAiConstants.*;

@Service
@Slf4j
public class AtlassianService {

    @Autowired
    private PageService pageService;

    @Autowired
    private AtlassianConnector atlassianConnector;

    @Autowired
    private IngestService ingestService;

    public void ingestContent() {
        JsonNode atlassianResponse = atlassianConnector.getAtlassianResponse(MyAiConstants.PAGES_BASE_URL, null);
        List<String> allPageIds = getAllPageIds(atlassianResponse);
        log.info("Total pages count: {}", allPageIds.size());
        ingestPages(allPageIds);
        log.info("Successfully completed ingestion");
    }

    private List<String> getAllPageIds(JsonNode atlassianResponse) {
        if (!atlassianResponse.getObject().has(MyAiConstants.RESULTS)) return Collections.emptyList();
        JSONArray results = atlassianResponse.getObject().getJSONArray(RESULTS);
        List<String> ids = new ArrayList<>();
        for (var resultObj : results) {
            JSONObject result = (JSONObject) resultObj;
            ids.add(result.getString(MyAiConstants.ID));
        }
        return ids;
    }

    private void ingestPages(List<String> allPageIds) {
        for (String id : allPageIds) {
            String pageById = MyAiConstants.PAGES_BASE_URL + "/" + id;
            Map<String, Object> queryParams = Map.of(MyAiConstants.BODY_FORMAT, MyAiConstants.ATLAS_DOC_FORMAT);
            JsonNode atlassianResponse = atlassianConnector.getAtlassianResponse(pageById, queryParams);
            JSONObject fields = extractRequiredFields(atlassianResponse);
            List<Document> documents = pageService.extractContent(fields);
            ingestService.ingestDocuments(documents);
            log.info("Completed ingesting page: {}", id);
        }
    }

    private JSONObject extractRequiredFields(JsonNode atlassianResponse) {
        JSONObject fields = new JSONObject();
        JSONObject object = atlassianResponse.getObject();
        JSONObject links = object.getJSONObject(_LINKS);
        String webui = links.getString(WEBUI);
        String base = links.getString(BASE);

        JSONObject body = object.getJSONObject(BODY);
        JSONObject atlasDocFormat = body.getJSONObject(ATLAS_DOC_FORMAT);
        String value = atlasDocFormat.getString(VALUE);
        JSONObject bodyObj = new JSONObject(value);

        fields.put(URL, base + webui);
        fields.put(ID, object.getString(ID));
        fields.put(TITLE, object.getString(TITLE));
        fields.put(CONTENT, bodyObj.getJSONArray(CONTENT));
        return fields;
    }

}
