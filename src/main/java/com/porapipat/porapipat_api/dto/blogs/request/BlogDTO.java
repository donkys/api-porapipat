package com.porapipat.porapipat_api.dto.blogs.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Data
public class BlogDTO {
    private String id;
    private String title;
    private String content;
    private String markdownFile;
    private String authorId;
    private Instant createdAt;
    private Instant updatedAt;
    private List<String> tags;
    private List<CommentDTO> comments;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getCreatedAtLocal() {
        return LocalDateTime.ofInstant(createdAt, ZoneId.of("Asia/Bangkok"));
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getUpdatedAtLocal() {
        return LocalDateTime.ofInstant(updatedAt, ZoneId.of("Asia/Bangkok"));
    }
}