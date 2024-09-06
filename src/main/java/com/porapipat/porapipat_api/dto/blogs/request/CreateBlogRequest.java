package com.porapipat.porapipat_api.dto.blogs.request;

import com.porapipat.porapipat_api.dto.blogs.response.BlogResponse;
import com.porapipat.porapipat_api.dto.blogs.response.BlogSearchResponse;
import com.porapipat.porapipat_api.entity.BlogEntity;
import lombok.Data;
import java.util.List;

@Data
public class CreateBlogRequest {
    private String title;
    private String content;
    private String markdownContent;
    private List<String> tags;
}

