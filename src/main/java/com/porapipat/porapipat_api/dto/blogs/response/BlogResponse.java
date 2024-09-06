package com.porapipat.porapipat_api.dto.blogs.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.porapipat.porapipat_api.dto.blogs.request.BlogDTO;
import com.porapipat.porapipat_api.dto.blogs.request.CommentDTO;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogResponse {
    private String id;
    private String title;
    private String content;
    private String markdownFile;
    private String authorId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Bangkok")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Bangkok")
    private Instant updatedAt;

    private List<String> tags;
    private List<CommentDTO> comments;
}
