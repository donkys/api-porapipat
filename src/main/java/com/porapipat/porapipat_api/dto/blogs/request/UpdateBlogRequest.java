package com.porapipat.porapipat_api.dto.blogs.request;

import lombok.Data;
import java.util.List;

@Data
public class UpdateBlogRequest {
    private String title;
    private String content;
    private String markdownContent;
    private List<String> tags;
}