package com.myai.service;

import com.mashape.unirest.http.JsonNode;
import com.myai.connector.AtlassianConnector;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.myai.constants.MyAiConstants.*;

@Service
public class FileService {

    @Autowired
    private AtlassianConnector atlassianConnector;

    @Value("${myai.properties.data.path}")
    private String path;

    void saveAttachments(JsonNode attachments) {
        JSONObject object = attachments.getObject();
        JSONArray results = object.getJSONArray(RESULTS);
        JSONObject links = object.getJSONObject(_LINKS);
        String base = links.getString(BASE);

        for (var result : results) {
            JSONObject resultObj = (JSONObject) result;
            String downloadLink = resultObj.getString(DOWNLOAD_LINK);
            String title = resultObj.getString(TITLE);
            String fileId = resultObj.getString(FILE_ID);

            try {
                InputStream atlassianBinaryResponse = atlassianConnector.getAtlassianBinaryResponse(base + downloadLink, null);
                FileUtils.copyInputStreamToFile(atlassianBinaryResponse, new File(path + fileId + HYPHEN + title));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public String getDownloadedFileContent(String id) {
        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            return paths.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(fileName -> fileName.contains(id))
                    .findFirst()
                    .map(this::extractChunksFromFiles)
                    .map(Document::text)
                    .orElse(Strings.EMPTY);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return Strings.EMPTY;
        }
    }



    Document extractChunksFromFiles(String path) {
        return FileSystemDocumentLoader.loadDocument(path, new ApacheTikaDocumentParser());
    }

}
