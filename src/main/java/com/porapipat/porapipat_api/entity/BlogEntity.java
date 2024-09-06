package com.porapipat.porapipat_api.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "blogs")
public class BlogEntity {
    @Id
    private String id;
    private String title;
    private String content;
    private String markdownFile;
    private String authorId;
    private Instant createdAt;
    private Instant updatedAt;
    private List<String> tags;
    private List<Comment> comments = new ArrayList<>();

    @Data
    public static class Comment {
        private String userId;
        private String comment;
        private Instant createdAt;
    }
}