package com.myai.service;

import com.mashape.unirest.http.JsonNode;
import com.myai.connector.AtlassianConnector;
import com.myai.constants.MyAiConstants;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.myai.constants.MyAiConstants.*;

@Service
public class PageService {

    @Autowired
    private AtlassianConnector atlassianConnector;

    @Autowired
    private FileService fileService;

    private final List<String> CONTENT_ELEMENTS = List.of(
            PARAGRAPH, BULLET_LIST, LIST_ITEM,
            MEDIA_GROUP, BLOCK_QUOTE, CODE_BLOCK,
            PANEL, TABLE, TABLE_ROW, TABLE_HEADER,
            TABLE_CELL
    );

    public List<Document> extractContent(JSONObject fields) {
        StringBuilder data = new StringBuilder();
        data.append(fields.getString(TITLE)).append(Strings.LINE_SEPARATOR);
        JsonNode attachments = atlassianConnector.getAtlassianResponse(String.format(MyAiConstants.ATTACHMENTS_BY_PAGE_ID, fields.getString(ID)), null);
        fileService.saveAttachments(attachments);
        extractAndAppend(fields.getJSONArray(CONTENT), data);
        return getDocument(data, fields.getString(URL), fields.getString(TITLE));
    }

    void extractAndAppend(JSONArray content, StringBuilder data) {

        for (var obj : content) {

            JSONObject contentElement = (JSONObject) obj;
            String type = contentElement.getString(TYPE);

            if (CONTENT_ELEMENTS.contains(type) && contentElement.has(CONTENT)) {
                JSONArray paraContent = contentElement.getJSONArray(CONTENT);
                extractAndAppend(paraContent, data);
            }

            if (StringUtils.equals(type, TEXT)) {
                String text = contentElement.getString(TEXT);
                data.append(text).append(Strings.LINE_SEPARATOR);
            }

            if ((StringUtils.equals(type, MEDIA) || StringUtils.equals(type, MEDIA_IN_LINE)) && contentElement.has(ATTRS)) {
                JSONObject attrs = contentElement.getJSONObject(ATTRS);
                String id = attrs.getString(ID);
                String fileContent = fileService.getDownloadedFileContent(id);
                data.append(Strings.LINE_SEPARATOR).append(fileContent);
            }

            if (StringUtils.equals(type, INLINE_CARD) && contentElement.has(ATTRS)) {
                JSONObject attrs = contentElement.getJSONObject(ATTRS);
                data.append(attrs.getString(URL));
            }

        }

    }

    private List<Document> getDocument(StringBuilder data, String url, String title) {
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<String> chunksOfData = splitter.split(new String(data), 1000);
        Metadata metadata = Metadata.metadata(URL, url).put(TITLE, title);
        return chunksOfData.stream().map(chunk -> new Document(chunk, metadata)).collect(Collectors.toList());
    }

}
