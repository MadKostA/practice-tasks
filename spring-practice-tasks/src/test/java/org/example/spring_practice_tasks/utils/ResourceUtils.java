package org.example.spring_practice_tasks.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ResourceUtils {

    private final ObjectMapper objectMapper;

    public String getJsonFromResources(String resourcesPath, Class<?> targetClass) throws IOException {
        InputStream resourceAsStream = getInputStreamFromResources(resourcesPath);

        Object reportInstance = objectMapper.readValue(resourceAsStream, targetClass);

        return objectMapper.writeValueAsString(reportInstance);
    }

    public <T> T getObjectFromResources(String resourcesPath, Class<T> targetClass) throws IOException {
        InputStream resourceAsStream = getInputStreamFromResources(resourcesPath);
        return objectMapper.readValue(resourceAsStream, targetClass);
    }

    public <T> T getObjectFromResources(String resourcesPath, TypeReference<T> targetClass) throws IOException {
        InputStream resourceAsStream = getInputStreamFromResources(resourcesPath);
        return objectMapper.readValue(resourceAsStream, targetClass);
    }

    public <T> T getGenericObjectFromResources(String resourcesPath, TypeReference<T> targetClass) throws IOException {
        InputStream resourceAsStream = getInputStreamFromResources(resourcesPath);
        return objectMapper.readValue(resourceAsStream, targetClass);
    }

    public InputStream getInputStreamFromResources(String resourcesPath) {
        return this.getClass().getClassLoader().getResourceAsStream(resourcesPath);
    }

    public String getWrappedJsonFileContent(String resourcesPath, Class<?> targetClass) throws IOException {
        String body = getJsonFromResources(resourcesPath, targetClass);
        return "{\n" +
                "  \"status\": true,\n" +
                "  \"response\": " +
                body + ",\n" +
                "  \"errors\": []\n" +
                "}";
    }


    public byte[] getFileByteArray(String resourcesPath) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(resourcesPath);
        InputStream fileInputStream = classPathResource.getInputStream();

        return StreamUtils.copyToByteArray(fileInputStream);
    }

    public <T> T getObjectFromString(String resourcesPath, Class<T> targetClass) throws com.fasterxml.jackson.core.JsonProcessingException {
        return objectMapper.readValue(resourcesPath, targetClass);
    }

    public <T> T getGenericObjectFromString(String object, TypeReference<T> targetClass) throws IOException {;
        return objectMapper.readValue(object, targetClass);
    }

    public String writeAsString(Object value) throws com.fasterxml.jackson.core.JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }

    public String getStringFromResources(String resourcesPath) {
        InputStream resourceAsStream = getInputStreamFromResources(resourcesPath);
        return new BufferedReader(new InputStreamReader(resourceAsStream))
                .lines().collect(Collectors.joining("\n"));
    }

    public InputStream getInputStreamFromFile(String resourcesPath) {
        InputStream initialStream = getClass().getResourceAsStream(resourcesPath);
        byte[] fileContent;
        try {
            assert initialStream != null;
            fileContent = initialStream.readAllBytes();
            initialStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ByteArrayInputStream(fileContent);
    }

    public String getFileContent(String filePath) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

}
