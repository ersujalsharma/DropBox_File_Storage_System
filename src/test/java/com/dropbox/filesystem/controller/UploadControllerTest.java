package com.dropbox.filesystem.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.dropbox.filesystem.service.S3PresignedUrlService;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private S3PresignedUrlService s3PresignedUrlService;

    @Test
    void initAndCompleteUpload() throws Exception {
        when(s3PresignedUrlService.generateUploadUrl(anyString())).thenReturn("https://signed-upload.example.com");

        String initPayload = """
                {
                  "userId": "user-1",
                  "fileName": "doc.txt",
                  "size": 11,
                  "chunkSizeBytes": 5
                }
                """;

        MvcResult initResult = mockMvc.perform(post("/api/upload/init")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(initPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalChunks").value(3))
                .andReturn();

        String body = initResult.getResponse().getContentAsString();
        String sessionId = body.split("\"sessionId\":\"")[1].split("\"")[0];

        String completePayload = """
                {
                  "sessionId": "%s",
                  "checksum": "abc123",
                  "etags": ["e1", "e2", "e3"],
                  "path": "/user-1/root/doc.txt"
                }
                """.formatted(sessionId);

        String completeResponse = mockMvc.perform(post("/api/upload/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(completePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.fileName").value("doc.txt"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(completeResponse).contains("abc123");
    }
}
